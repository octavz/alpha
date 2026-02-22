import { useState } from 'react'
import axios from 'axios'
import {
  Container,
  Card,
  Title,
  Button,
  Group,
  Stack,
  TextInput,
  PasswordInput,
  Tabs,
  Alert,
  List,
  ThemeIcon,
  Divider,
  LoadingOverlay,
  Text,
  Paper,
  Box,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconCheck, IconAlertCircle, IconUser, IconLock, IconMail, IconHeartbeat, IconLogout, IconLogin, IconUserPlus } from '@tabler/icons-react'

// API client configuration
const api = axios.create({
  baseURL: 'http://localhost:3000/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

function App() {
  const [user, setUser] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState<string | null>('login')

  // Login form
  const loginForm = useForm({
    initialValues: {
      email: '',
      password: '',
    },
    validate: {
      email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
      password: (value) => (value.length < 6 ? 'Password must be at least 6 characters' : null),
    },
  })

  // Register form
  const registerForm = useForm({
    initialValues: {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
    },
    validate: {
      name: (value) => (value.trim().length < 2 ? 'Name must be at least 2 characters' : null),
      email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
      password: (value) => (value.length < 6 ? 'Password must be at least 6 characters' : null),
      confirmPassword: (value, values) =>
        value !== values.password ? 'Passwords do not match' : null,
    },
  })

  const handleLogin = async (values: typeof loginForm.values) => {
    setLoading(true)
    try {
      const response = await api.post('/auth/login', values)
      if (response.data.success) {
        setUser(response.data.data?.user)
        notifications.show({
          title: 'Login successful!',
          message: `Welcome back, ${response.data.data?.user.name}!`,
          color: 'teal',
          icon: <IconCheck size={20} />,
        })
      } else {
        notifications.show({
          title: 'Login failed',
          message: response.data.error?.message || 'Invalid credentials',
          color: 'red',
          icon: <IconAlertCircle size={20} />,
        })
      }
    } catch (err: any) {
      notifications.show({
        title: 'Error',
        message: err.response?.data?.error?.message || err.message || 'An error occurred',
        color: 'red',
        icon: <IconAlertCircle size={20} />,
      })
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (values: typeof registerForm.values) => {
    setLoading(true)
    try {
      const { confirmPassword, ...registerData } = values
      const response = await api.post('/auth/register', registerData)
      if (response.data.success) {
        setUser(response.data.data?.user)
        notifications.show({
          title: 'Registration successful!',
          message: `Welcome to Alpha, ${response.data.data?.user.name}!`,
          color: 'teal',
          icon: <IconCheck size={20} />,
        })
      } else {
        notifications.show({
          title: 'Registration failed',
          message: response.data.error?.message || 'Registration failed',
          color: 'red',
          icon: <IconAlertCircle size={20} />,
        })
      }
    } catch (err: any) {
      notifications.show({
        title: 'Error',
        message: err.response?.data?.error?.message || err.message || 'An error occurred',
        color: 'red',
        icon: <IconAlertCircle size={20} />,
      })
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = async () => {
    setLoading(true)
    try {
      await api.post('/auth/logout')
      setUser(null)
      notifications.show({
        title: 'Logged out',
        message: 'You have been successfully logged out',
        color: 'blue',
      })
    } catch (err: any) {
      notifications.show({
        title: 'Error',
        message: err.response?.data?.error?.message || err.message || 'Logout failed',
        color: 'red',
        icon: <IconAlertCircle size={20} />,
      })
    } finally {
      setLoading(false)
    }
  }

  const handleHealthCheck = async () => {
    setLoading(true)
    try {
      const response = await api.get('/health')
      if (response.data.success) {
        notifications.show({
          title: 'Health Check',
          message: `Backend is healthy: ${response.data.data?.message}`,
          color: 'teal',
          icon: <IconHeartbeat size={20} />,
        })
      } else {
        notifications.show({
          title: 'Health Check Failed',
          message: response.data.error?.message || 'Backend is not responding',
          color: 'red',
          icon: <IconAlertCircle size={20} />,
        })
      }
    } catch (err: any) {
      notifications.show({
        title: 'Health Check Error',
        message: err.response?.data?.error?.message || err.message || 'Cannot reach backend',
        color: 'red',
        icon: <IconAlertCircle size={20} />,
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Box style={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
      <LoadingOverlay visible={loading} />
      
      {/* Header */}
      <Paper shadow="sm" p="md" radius={0} style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
        <Container size="lg">
          <Group justify="space-between">
            <Group>
              <ThemeIcon size="lg" variant="white">
                <IconUser size={24} />
              </ThemeIcon>
              <div>
                <Title order={3} c="white">Alpha Application</Title>
                <Text size="xs" c="white" opacity={0.9}>React + Mantine + Elysia</Text>
              </div>
            </Group>
            
            {user && (
              <Group>
                <ThemeIcon color="white" variant="light">
                  <IconUser size={16} />
                </ThemeIcon>
                <div>
                  <Text size="sm" fw={500} c="white">{user.name}</Text>
                  <Text size="xs" c="white" opacity={0.8}>{user.email}</Text>
                </div>
              </Group>
            )}
          </Group>
        </Container>
      </Paper>

      <Container size="lg" py="xl">
        {user ? (
          <Stack gap="lg">
            {/* User Dashboard */}
            <Card shadow="sm" padding="lg" radius="md" withBorder>
              <Title order={2} mb="md">Welcome, {user.name}! 👋</Title>
              <Text c="dimmed" mb="xl">
                You are successfully logged into the Alpha application. 
                Explore the features and manage your account.
              </Text>
              
              <Group grow>
                <Card withBorder>
                  <Group justify="apart" mb="xs">
                    <Text fw={500}>Account Info</Text>
                    <ThemeIcon color="blue" variant="light">
                      <IconUser size={16} />
                    </ThemeIcon>
                  </Group>
                  <Stack gap="xs">
                    <Text size="sm"><strong>Name:</strong> {user.name}</Text>
                    <Text size="sm"><strong>Email:</strong> {user.email}</Text>
                    <Text size="sm"><strong>Status:</strong> Active</Text>
                  </Stack>
                </Card>
                
                <Card withBorder>
                  <Group justify="apart" mb="xs">
                    <Text fw={500}>Quick Stats</Text>
                    <ThemeIcon color="green" variant="light">
                      <IconHeartbeat size={16} />
                    </ThemeIcon>
                  </Group>
                  <Stack gap="xs">
                    <Text size="sm"><strong>Session:</strong> Active</Text>
                    <Text size="sm"><strong>Last Login:</strong> Just now</Text>
                    <Text size="sm"><strong>Role:</strong> User</Text>
                  </Stack>
                </Card>
              </Group>
              
              <Group mt="xl">
                <Button
                  leftSection={<IconHeartbeat size={16} />}
                  variant="light"
                  color="blue"
                  onClick={handleHealthCheck}
                  loading={loading}
                >
                  Health Check
                </Button>
                
                <Button
                  leftSection={<IconLogout size={16} />}
                  variant="light"
                  color="red"
                  onClick={handleLogout}
                  loading={loading}
                >
                  Logout
                </Button>
              </Group>
            </Card>
            
            {/* Features */}
            <Card shadow="sm" padding="lg" radius="md" withBorder>
              <Title order={3} mb="md">Application Features</Title>
              <List
                spacing="sm"
                size="sm"
                center
                icon={
                  <ThemeIcon color="blue" size={20} radius="xl">
                    <IconCheck size={12} />
                  </ThemeIcon>
                }
              >
                <List.Item>Built with React + TypeScript + Vite</List.Item>
                <List.Item>Uses Mantine UI components</List.Item>
                <List.Item>Connects to Elysia backend with Swagger</List.Item>
                <List.Item>Full authentication system (login/register/logout)</List.Item>
                <List.Item>Health check monitoring</List.Item>
                <List.Item>Responsive design</List.Item>
                <List.Item>Real-time notifications</List.Item>
                <List.Item>Form validation with Mantine Form</List.Item>
              </List>
            </Card>
          </Stack>
        ) : (
          /* Auth Forms */
          <Card shadow="sm" padding="lg" radius="md" withBorder>
            <Title order={2} mb="md" ta="center">
              Welcome to Alpha Application
            </Title>
            <Text c="dimmed" mb="xl" ta="center">
              Please login or register to continue
            </Text>
            
            <Tabs value={activeTab} onChange={setActiveTab}>
              <Tabs.List grow>
                <Tabs.Tab value="login" leftSection={<IconLogin size={16} />}>Login</Tabs.Tab>
                <Tabs.Tab value="register" leftSection={<IconUserPlus size={16} />}>Register</Tabs.Tab>
              </Tabs.List>
              
              <Tabs.Panel value="login" pt="md">
                <form onSubmit={loginForm.onSubmit(handleLogin)}>
                  <Stack gap="md">
                    <TextInput
                      label="Email"
                      placeholder="your@email.com"
                      required
                      leftSection={<IconMail size={16} />}
                      {...loginForm.getInputProps('email')}
                    />
                    
                    <PasswordInput
                      label="Password"
                      placeholder="Your password"
                      required
                      leftSection={<IconLock size={16} />}
                      {...loginForm.getInputProps('password')}
                    />
                    
                    <Group justify="right" mt="md">
                      <Button
                        type="submit"
                        leftSection={<IconLogin size={16} />}
                        loading={loading}
                      >
                        Login
                      </Button>
                    </Group>
                  </Stack>
                </form>
              </Tabs.Panel>
              
              <Tabs.Panel value="register" pt="md">
                <form onSubmit={registerForm.onSubmit(handleRegister)}>
                  <Stack gap="md">
                    <TextInput
                      label="Full Name"
                      placeholder="John Doe"
                      required
                      leftSection={<IconUser size={16} />}
                      {...registerForm.getInputProps('name')}
                    />
                    
                    <TextInput
                      label="Email"
                      placeholder="your@email.com"
                      required
                      leftSection={<IconMail size={16} />}
                      {...registerForm.getInputProps('email')}
                    />
                    
                    <PasswordInput
                      label="Password"
                      placeholder="Your password"
                      required
                      leftSection={<IconLock size={16} />}
                      {...registerForm.getInputProps('password')}
                    />
                    
                    <PasswordInput
                      label="Confirm Password"
                      placeholder="Confirm your password"
                      required
                      leftSection={<IconLock size={16} />}
                      {...registerForm.getInputProps('confirmPassword')}
                    />
                    
                    <Group justify="right" mt="md">
                      <Button
                        type="submit"
                        leftSection={<IconUserPlus size={16} />}
                        loading={loading}
                      >
                        Register
                      </Button>
                    </Group>
                  </Stack>
                </form>
              </Tabs.Panel>
            </Tabs>
            
            <Divider my="xl" label="Or" labelPosition="center" />
            
            <Alert icon={<IconHeartbeat size={16} />} title="Backend Status" color="blue" variant="light">
              <Group justify="apart">
                <Text size="sm">Check if the backend API is running properly</Text>
                <Button
                  size="xs"
                  variant="light"
                  leftSection={<IconHeartbeat size={14} />}
                  onClick={handleHealthCheck}
                  loading={loading}
                >
                  Test Connection
                </Button>
              </Group>
            </Alert>
          </Card>
        )}
      </Container>

      {/* Footer */}
      <Paper shadow="sm" p="md" radius={0} style={{ backgroundColor: '#1a202c' }}>
        <Container size="lg">
          <Group justify="space-between">
            <Text size="sm" c="dimmed">
              © {new Date().getFullYear()} Alpha Application
            </Text>
            <Group gap="xs">
              <Text size="xs" c="dimmed">Backend: localhost:3000</Text>
              <Text size="xs" c="dimmed">•</Text>
              <Text size="xs" c="dimmed">Frontend: localhost:5173</Text>
            </Group>
          </Group>
        </Container>
      </Paper>
    </Box>
  )
}

export default App