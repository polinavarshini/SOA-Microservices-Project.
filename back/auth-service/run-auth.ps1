Set-Location "C:\soa\soa\back\auth-service"
Get-Content .env | ForEach-Object {
    if ($_ -match '^(.*?)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
mvn spring-boot:run > auth-service.log 2>&1
