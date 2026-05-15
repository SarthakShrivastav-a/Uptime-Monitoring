# Frontend Authentication Guide

This guide provides comprehensive information for implementing authentication in the frontend application.

## Base URL
All authentication endpoints are prefixed with `/api/auth`

## Authentication Endpoints

### 1. Sign In (Login)

**Endpoint:** `POST /api/auth/signin`

**Request Body:**
```typescript
interface LoginRequest {
  email: string;
  password: string;
}
```

**Example Request:**
```javascript
const loginData = {
  email: "user@example.com",
  password: "yourPassword123"
};
```

**Response:**
- Success (200 OK): Returns JWT token as string
- Error (400 Bad Request): Returns error message as string

**Example Implementation:**
```typescript
async function login(email: string, password: string) {
  try {
    const response = await fetch('/api/auth/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const token = await response.text();
    // Store token in localStorage or secure storage
    localStorage.setItem('authToken', token);
    return token;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}
```

### 2. Registration (Sign Up)

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```typescript
interface SignUpRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  companyName: string;
}
```

**Example Request:**
```javascript
const signUpData = {
  email: "user@example.com",
  password: "yourPassword123",
  firstName: "John",
  lastName: "Doe",
  companyName: "ACME Corp"
};
```

**Response:**
- Success (200 OK): Returns success message as string
- Error (400 Bad Request): Returns error message as string

**Example Implementation:**
```typescript
async function register(userData: SignUpRequest) {
  try {
    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage);
    }

    const message = await response.text();
    return message;
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
}
```

## Error Handling

The API may return the following error messages:
- "Invalid username or password" - When login credentials are incorrect
- "User already exists with this email" - When trying to register with an existing email

## Best Practices

1. **Token Storage:**
   - Store the JWT token securely (preferably in HttpOnly cookies or secure storage)
   - Include the token in subsequent API requests in the Authorization header:
     ```javascript
     headers: {
       'Authorization': `Bearer ${token}`,
       'Content-Type': 'application/json'
     }
     ```

2. **Form Validation:**
   - Validate email format before submission
   - Ensure password meets minimum security requirements
   - Validate all required fields are present

3. **Error Handling:**
   - Display user-friendly error messages
   - Implement proper error states in the UI
   - Handle network errors gracefully

4. **Security:**
   - Use HTTPS for all API calls
   - Never store passwords in plain text
   - Implement proper CSRF protection if required
   - Consider implementing rate limiting on the frontend

## Example Usage

```typescript
// Login example
const handleLogin = async (email: string, password: string) => {
  try {
    const token = await login(email, password);
    // Handle successful login (e.g., redirect to dashboard)
    console.log('Login successful');
  } catch (error) {
    // Handle login error
    console.error('Login failed:', error);
  }
};

// Registration example
const handleRegister = async (userData: SignUpRequest) => {
  try {
    const message = await register(userData);
    // Handle successful registration
    console.log('Registration successful:', message);
  } catch (error) {
    // Handle registration error
    console.error('Registration failed:', error);
  }
};
```

## TypeScript Interfaces

```typescript
// Request interfaces
interface LoginRequest {
  email: string;
  password: string;
}

interface SignUpRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  companyName: string;
}

// Response interfaces
interface AuthResponse {
  token: string;
  message: string;
}
``` 