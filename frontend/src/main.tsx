import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Home from './views/Home'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Unsubscribe from './views/Unsubscribe'

const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />
  },
  {
    path: '/unsubscribe',
    element: <Unsubscribe />
  }
])
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
)
