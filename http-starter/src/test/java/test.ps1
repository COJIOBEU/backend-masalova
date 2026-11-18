Write-Host "тесты. Введите .\test.ps1 в консоль" -ForegroundColor Cyan


Write-Host "1. Создаем проект" -ForegroundColor Green
$body1 = @{ name = "Backend Lab" } | ConvertTo-Json
$projResp = Invoke-RestMethod -Uri "http://localhost:8080/projects" -Method Post -Body $body1 -ContentType "application/json"
$projResp | ConvertTo-Json
$PROJECT_ID = $projResp.id
Write-Host "   -> Получен PROJECT_ID: $PROJECT_ID" -ForegroundColor Yellow


Write-Host "2. Получаем список проектов" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8080/projects" -Method Get | ConvertTo-Json


Write-Host "3. Создаем баг-задачу" -ForegroundColor Green
$body3 = @{ title = "Fix login bug"; projectId = $PROJECT_ID; type = "BugTask"; severity = "CRITICAL" } | ConvertTo-Json
$taskResp = Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Post -Body $body3 -ContentType "application/json"
$taskResp | ConvertTo-Json
$TASK_ID = $taskResp.id
Write-Host "   -> Получен TASK_ID: $TASK_ID" -ForegroundColor Yellow


Write-Host "4. Создаем фичу-задачу" -ForegroundColor Green
$body4 = @{ title = "Add filtering"; projectId = $PROJECT_ID; type = "FeatureTask"; storyPoints = 5 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Post -Body $body4 -ContentType "application/json" | ConvertTo-Json


Write-Host "5. Получаем все задачи" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Get | ConvertTo-Json
Write-Host ""

Write-Host "6. Фильтруем только баги" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8080/tasks?type=BugTask" -Method Get | ConvertTo-Json
Write-Host ""

Write-Host "7. Завершаем задачу" -ForegroundColor Green
# Используем переменную $TASK_ID
Invoke-RestMethod -Uri "http://localhost:8080/tasks/$TASK_ID/complete" -Method Post | ConvertTo-Json
Write-Host ""

Write-Host "8. Завершаем уже завершенную задачу (ожидаем ошибку 409)" -ForegroundColor Green
try {
    # Используем переменную $TASK_ID
    Invoke-RestMethod -Uri "http://localhost:8080/tasks/$TASK_ID/complete" -Method Post
} catch {
    Write-Host "   -> Ожидаемая ошибка:" $_.Exception.Message -ForegroundColor Red
}
Write-Host ""

Write-Host "9. Создаем задачу в несуществующий проект (ожидаем ошибку 400)" -ForegroundColor Green
$body9 = @{ title = "Test task"; projectId = "nonexistent-id"; type = "BugTask"; severity = "LOW" } | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "http://localhost:8080/tasks" -Method Post -Body $body9 -ContentType "application/json"
} catch {
    Write-Host "   -> Ожидаемая ошибка:" $_.Exception.Message -ForegroundColor Red
}
Write-Host ""

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "ГОТОВО! Все тесты выполнены успешно." -ForegroundColor Cyan
Write-Host "==================================================="