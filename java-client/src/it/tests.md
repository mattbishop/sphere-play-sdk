## Integration Tests

Add the concrete environment variables for your _development_ project to your console.

```bash
export SDK_IT_PROJECT_KEY=""
export SDK_IT_CLIENT_ID=""
export SDK_IT_CLIENT_SECRET=""
```

Run in the same shell ```sbt ~it:test```.

## Enable debugging

To log the HTTP requests to sphere, set in src/it/resources/logback.xml ```<logger name="sphere" level="trace"/>```.