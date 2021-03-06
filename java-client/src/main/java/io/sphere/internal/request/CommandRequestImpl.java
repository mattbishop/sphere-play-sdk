package io.sphere.internal.request;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import io.sphere.client.exceptions.SphereBackendException;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.SphereResult;
import io.sphere.internal.command.Command;
import io.sphere.internal.util.Iso8601JsonSerializer;
import io.sphere.internal.util.Util;
import io.sphere.client.CommandRequest;

import com.google.common.util.concurrent.ListenableFuture;
import net.jcip.annotations.Immutable;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@Immutable
public class CommandRequestImpl<T> implements CommandRequest<T>, TestableRequest {
    final RequestHolder<T> requestHolder;
    final Command command;
    final TypeReference<T> jsonParserTypeRef;
    final @Nullable Function<SphereBackendException, SphereException> transformError;

    public CommandRequestImpl(
            @Nonnull RequestHolder<T> requestHolder,
            @Nonnull Command command,
            @Nonnull TypeReference<T> jsonParserTypeRef) {
        this(requestHolder, command, jsonParserTypeRef, null);
    }

    public CommandRequestImpl(
            @Nonnull RequestHolder<T> requestHolder,
            @Nonnull Command command,
            @Nonnull TypeReference<T> jsonParserTypeRef,
            Function<SphereBackendException, SphereException> transformError)
    {
        if (requestHolder == null) throw new NullPointerException("requestHolder");
        if (command == null) throw new NullPointerException("command");
        if (jsonParserTypeRef == null) throw new NullPointerException("jsonParserTypeRef");
        //TODO the construction of an ObjectMapper and a Module per Request is expensive
        ObjectMapper mapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider().addFilter("changeAddressIdFilter",
                SimpleBeanPropertyFilter.SerializeExceptFilter.serializeAllExcept("id"));
        SimpleModule testModule = new SimpleModule("Iso8601JsonSerializerModule", new Version(1, 0, 0, null));
        testModule.addSerializer(new Iso8601JsonSerializer());
        mapper.registerModule(testModule);
        ObjectWriter jsonWriter = mapper.writer(filters);
        try {
            this.requestHolder = requestHolder.setBody(jsonWriter.writeValueAsString(command));
        } catch (IOException e) {
            throw Util.toSphereException(e);
        }
        this.command = command;
        this.jsonParserTypeRef = jsonParserTypeRef;
        this.transformError = transformError;
    }

    @Override public T execute() {
        return Util.syncResult(executeAsync());
    }

    @Override public ListenableFuture<SphereResult<T>> executeAsync() {
        return Futures.transform(RequestExecutor.execute(requestHolder, jsonParserTypeRef), new Function<SphereResultRaw<T>, SphereResult<T>>() {
            public SphereResult<T> apply(SphereResultRaw<T> rawResult) {
                return SphereResult.withSpecificError(rawResult, transformError);
            }
        });
    }

    @Override public CommandRequest<T> withErrorHandling(@Nonnull Function<SphereBackendException, SphereException> transformError) {
        if (requestHolder == null) throw new NullPointerException("transformError");
        return new CommandRequestImpl<T>(this.requestHolder, this.command, this.jsonParserTypeRef, transformError);
    }

    /** The command object that will be sent, for testing purposes. */
    public Command getCommand() {
        return this.command;
    }

    // testing purposes
    @Override public TestableRequestHolder getRequestHolder() {
        return requestHolder;
    }

    // logging and debugging purposes
    @Override public String toString() {
        return requestHolder.toString();
    }
}
