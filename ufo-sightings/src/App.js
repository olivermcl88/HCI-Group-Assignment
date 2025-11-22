import Typography from "@mui/material/Typography";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Assignments from "./Assignments";

function HomePage() {
  return (
    <div>
      <Typography variant="h6">UFO Sightings</Typography>
      <List>
        <ListItem component={Link} to="/assignments" sx={{ cursor: 'pointer' }}>
          <ListItemText 
            primary={<Typography color="primary">Assignments</Typography>}
          />
        </ListItem>
      </List>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/assignments" element={<Assignments />} />
      </Routes>
    </Router>
  );
}

export default App;
