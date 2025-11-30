#!/bin/bash

echo "======================================"
echo "DailyCat Setup and Testing Script"
echo "======================================"
echo ""

# Check if CAT_API_KEY is set
if [ -z "$CAT_API_KEY" ]; then
    echo "⚠️  CAT_API_KEY environment variable not set"
    echo "   Run: export CAT_API_KEY='your-api-key'"
    echo ""
fi

# Start PostgreSQL
echo "1. Starting PostgreSQL container..."
sudo docker-compose up -d
sleep 2

# Check if PostgreSQL is ready
echo ""
echo "2. Checking PostgreSQL status..."
sudo docker-compose ps

# Start Spring Boot app in background
echo ""
echo "3. Starting Spring Boot application..."
echo "   (This will take a few seconds...)"
mvn spring-boot:run > /tmp/dailycat.log 2>&1 &
APP_PID=$!
echo "   Application PID: $APP_PID"

# Wait for app to start
echo ""
echo "4. Waiting for application to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/cat-of-the-day > /dev/null 2>&1; then
        echo "   ✓ Application is ready!"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""

# Sync cats from API
echo ""
echo "5. Syncing 10 cats from The Cat API to database..."
SYNC_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/admin/sync?count=10")
echo "   Response: $SYNC_RESPONSE"

# Test random cat endpoint
echo ""
echo "6. Testing random cat endpoint..."
CAT_RESPONSE=$(curl -s http://localhost:8080/api/cat-of-the-day)
echo "   Cat ID: $(echo $CAT_RESPONSE | grep -o '"id":"[^"]*"' | head -1)"

# Check database
echo ""
echo "7. Checking database contents..."
sudo docker exec dailycat-db psql -U dailycat -d dailycat -c "SELECT COUNT(*) as cat_count FROM cats;"
sudo docker exec dailycat-db psql -U dailycat -d dailycat -c "SELECT COUNT(*) as breed_count FROM breeds;"

echo ""
echo "======================================"
echo "✓ Setup Complete!"
echo "======================================"
echo ""
echo "Next steps:"
echo "  - Open http://localhost:8080 in your browser"
echo "  - Check logs: tail -f /tmp/dailycat.log"
echo "  - Stop app: kill $APP_PID"
echo "  - Stop database: sudo docker-compose down"
echo ""
