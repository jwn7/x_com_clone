// src/App.jsx
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import FeedPage from "./pages/FeedPage";
import ComposePage from "./pages/ComposePage";
import UserPage from "./pages/UserPage"; 

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/feed" element={<FeedPage />} />
        <Route path="/compose" element={<ComposePage />} />
        <Route path="/userpage" element={<UserPage />} />
      </Routes>
    </Router>
  );
}

export default App;
