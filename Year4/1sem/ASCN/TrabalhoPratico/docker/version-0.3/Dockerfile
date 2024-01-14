# Use Ubuntu 22.04 as base image
FROM ubuntu:22.04

# Set frontend. We do not want any interactive functions.
ENV DEBIAN_FRONTEND=noninteractive
ENV seed_database=false

# Install software-properties-common for add-apt-repository and update the package list
RUN apt-get update && \
    apt-get install -y software-properties-common

# Add the repository for PHP 8.2
RUN add-apt-repository ppa:ondrej/php && \
    apt-get update

# Install PHP 8.2 and required extensions
RUN apt-get install -y \
    php8.2 \
    php8.2-mysql \
    php8.2-mbstring \
    php8.2-xml \
    php8.2-curl \
    php8.2-zip \
    php8.2-tokenizer \
    php8.2-gd \
    php8.2-bcmath

# Install Composer
RUN php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" && \
    php composer-setup.php && \
    mv composer.phar /usr/local/bin/composer && \
    php -r "unlink('composer-setup.php');"

# Install git and mysql-client
RUN apt-get install -y git npm nodejs mysql-client

# Clone the Laravel.io repository into /app
RUN git clone https://github.com/laravelio/laravel.io.git app/

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Add a new user 'myuser'
RUN useradd -m myuser

# Change ownership of the /app directory
USER root
RUN chown -R myuser:myuser /app

# Switch to 'myuser'
USER myuser

# Install Composer dependencies
RUN composer install

# Move .env.example to .env
RUN mv .env.example .env

# Remove line 91 from composer.json
RUN sed -i "88d" composer.json

RUN sed -i "21s/.*/'driver' => env('SESSION_DRIVER', 'database'),/" config/session.php

# Run composer setup
RUN composer setup

# Expose port 8000
EXPOSE 8000

# Start the Laravel application
CMD ["php", "artisan", "serve", "--host", "0.0.0.0", "--port","8000"]
