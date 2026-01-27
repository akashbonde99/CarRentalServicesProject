
# API Base URL
$contentType = "application/json"
$baseUrl = "http://localhost:8080/api"

function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [string]$Token = $null,
        [string]$Description
    )
    
    Write-Host "Testing: $Description" -ForegroundColor Cyan
    
    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    try {
        $params = @{
            Uri         = $Url
            Method      = $Method
            ContentType = $contentType
        }
        if ($Body) {
            $params["Body"] = ($Body | ConvertTo-Json -Depth 5)
        }
        if ($headers.Count -gt 0) {
            $params["Headers"] = $headers
        }

        $response = Invoke-RestMethod @params
        Write-Host "SUCCESS" -ForegroundColor Green
        return $response
    }
    catch {
        Write-Host "FAILED: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
            $text = $reader.ReadToEnd()
            Write-Host "Response Body: $text" -ForegroundColor Red
        }
        return $null
    }
}

# 1. Register Admin
$adminUser = @{
    name = "Admin User"
    email = "admin@test.com"
    password = "password123"
    role = "ADMIN"
}
$regAdmin = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/register" -Body $adminUser -Description "Register Admin"

# 2. Login Admin
$adminLogin = @{
    email = "admin@test.com"
    password = "password123"
}
$loginResponseAdmin = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Body $adminLogin -Description "Login Admin"
Write-Host "Debug Login Response: $($loginResponseAdmin | ConvertTo-Json -Depth 5)" -ForegroundColor Yellow
$adminToken = $loginResponseAdmin.data.token

if (-not $adminToken) {
    Write-Host "Admin Login Failed - No Token" -ForegroundColor Red
    # Try different structure if token is directly in response or elsewhere
    $adminToken = $loginResponseAdmin.token 
}
Write-Host "Admin Token: $adminToken"

# 3. Add Car (Admin)
if ($adminToken) {
    $car = @{
        brand = "Toyota"
        model = "Camry"
        registrationNumber = "MH12AB1234"
        city = "Pune"
        pickupAddress = "Airport"
        description = "Luxury Sedan"
        pricePerDay = 500.0
        fuelType = "PETROL"
        carType = "SEDAN"
        status = "AVAILABLE"
    }
    $addCarResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/cars" -Body $car -Token $adminToken -Description "Add Car (Admin)"
    $carId = $addCarResponse.data.carId # Assuming structure
}

# 4. Register Customer
$customerUser = @{
    name = "Customer User"
    email = "customer@test.com"
    password = "password123"
    drivingLicence = "DL1234567890"
    role = "CUSTOMER"
}
$regCustomer = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/register" -Body $customerUser -Description "Register Customer"

# 5. Login Customer
$customerLogin = @{
    email = "customer@test.com"
    password = "password123"
}
$loginResponseCustomer = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Body $customerLogin -Description "Login Customer"
$customerToken = $loginResponseCustomer.data.token
Write-Host "Customer Token: $customerToken"

# 6. Search Cars (Customer)
if ($customerToken) {
    $searchResponse = Test-Endpoint -Method "GET" -Url "$baseUrl/cars/search/fuel/PETROL" -Token $customerToken -Description "Search Cars by Fuel Type"
    Write-Host "Cars Found: $($searchResponse.data.Count)"
}

# 7. Book Car (Customer)
if ($customerToken -and $carId) {
    # Need to verify BookingRequestDTO structure from previous interaction or best guess
    # BookingRequestDTO: carId, pickupDate, dropDate
    $booking = @{
        carId = $carId
        pickupDate = (Get-Date).ToString("yyyy-MM-dd")
        dropDate = (Get-Date).AddDays(2).ToString("yyyy-MM-dd")
    }
    $bookResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/bookings" -Body $booking -Token $customerToken -Description "Book Car"
    if ($bookResponse.success) {
        Write-Host "FINAL VERIFICATION: ALL FLOWS PASSED" -ForegroundColor Green
    } else {
        Write-Host "Booking Failed" -ForegroundColor Red
    }
}
