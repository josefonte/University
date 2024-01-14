#!/bin/bash

if [ "$seed_database" = "true" ]; then
    echo "Seeding the database"
    php artisan migrate --seed
else
    echo "Not seeding the database"
fi

# Start the Laravel application
php artisan serve --host 0.0.0.0 --port 8000
