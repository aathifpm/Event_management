# Deployment Guide - Render Platform

## Prerequisites
- GitHub repository with your Event Management application
- Render account (free tier available)
- MySQL database already configured (srv1880.hstgr.io)

## Step-by-Step Deployment

### 1. Connect GitHub Repository to Render

1. Log in to your [Render Dashboard](https://dashboard.render.com/)
2. Click "New +" and select "Web Service"
3. Connect your GitHub account if not already connected
4. Select your Event Management repository
5. Choose the branch (usually `master` or `main`)

### 2. Configure Web Service Settings

**Basic Settings:**
- **Name**: `event-management-app` (or your preferred name)
- **Runtime**: `Docker`
- **Region**: Choose closest to your users
- **Branch**: `master` or `main`
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -Dserver.port=$PORT -jar target/*.jar`

**Advanced Settings:**
- **Plan**: Free (or upgrade as needed)
- **Auto-Deploy**: Yes (enabled for CI/CD)

### 3. Set Environment Variables

In Render Dashboard, go to your service → Environment tab and add:

```
SPRING_DATASOURCE_URL=jdbc:mysql://srv1880.hstgr.io:3306/u518745130_CEM
SPRING_DATASOURCE_USERNAME=u518745130_CEM
SPRING_DATASOURCE_PASSWORD=your_actual_database_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
SPRING_SECURITY_REMEMBER_ME_KEY=your_secret_key_here
```

### 4. Deploy

1. Click "Create Web Service"
2. Render will automatically:
   - Clone your repository
   - Build the Docker image
   - Deploy your application
   - Provide a public URL

### 5. Verify Deployment

- Check the deployment logs in Render dashboard
- Visit your application URL
- Test key functionality:
  - Login page: `https://your-app.onrender.com/auth/login`
  - Registration: `https://your-app.onrender.com/auth/register`
  - Dashboard: `https://your-app.onrender.com/dashboard`
  - Admin users: `https://your-app.onrender.com/admin/users`
  - Admin clubs: `https://your-app.onrender.com/clubs/admin`

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/deploy.yml`) will:

1. **Test Stage**: Run unit tests on every push/PR
2. **Build Stage**: Create JAR file for master/main branch
3. **Deploy Stage**: Trigger automatic deployment to Render

### Automatic Deployment Flow

```
Code Push → GitHub → GitHub Actions → Tests Pass → Build → Render Auto-Deploy
```

## Database Configuration

Your MySQL database is already configured:
- **Host**: srv1880.hstgr.io
- **Port**: 3306
- **Database**: u518745130_CEM
- **Username**: u518745130_CEM

The application will automatically:
- Connect to your existing database
- Update schema if needed (DDL auto-update enabled)
- Preserve existing data

## Troubleshooting

### Common Issues:

1. **Build Fails**:
   - Check Java version (should be 21)
   - Verify Maven wrapper permissions
   - Check build logs in Render dashboard

2. **Database Connection Issues**:
   - Verify environment variables
   - Check database server status
   - Ensure network connectivity

3. **Application Won't Start**:
   - Check memory settings (JAVA_OPTS)
   - Verify START_COMMAND
   - Review application logs

### Useful Commands for Local Testing:

```bash
# Test Docker build locally
docker build -t event-management .
docker run -p 8080:8080 event-management

# Test with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=production
```

## Monitoring

- **Application URL**: Will be provided by Render
- **Logs**: Available in Render dashboard
- **Metrics**: Basic metrics available in free tier
- **Health Check**: Configured for root path "/"

## Security Notes

- Environment variables are encrypted in Render
- HTTPS is automatically provided
- Database credentials are secure
- Remember-me key should be unique per deployment

## Scaling

- Free tier: 750 hours/month
- Paid tiers: Auto-scaling available
- Database: Already configured for production load
- Static files: Served by Spring Boot (consider CDN for high traffic)

---

**Next Steps:**
1. Push your code to GitHub
2. Follow the deployment steps above
3. Configure environment variables
4. Monitor the first deployment
5. Test all admin functionality

Your Event Management application will be live and accessible via the Render-provided URL!
