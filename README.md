# Spring reactive programming demo

Projects:
1. `console-dashboard` — spring boot app for grab metrics from all service and visualize it in terminal. `./gradlew :console-dashboard:bootRun`
2. `metrics-starter` — starter for expose metrics from service. Use this metrics in console-dashboard
3. `speed-adjuster-starter` — starter. Provide shared logic for request(n) through http and use in each demo service
4. `pechkin-service` — generate letter and push to `big-brother-service`
5. `big-brother-service` — decode letter and send it to `agent-smith-service`
6. `agent-smith-service` — analyse decoded letter and send feedback to `pechking-service`

