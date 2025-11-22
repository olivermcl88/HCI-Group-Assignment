import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";

function Assignments() {
  return (
    <div>
      <Typography variant="h4">Assignments</Typography>
      <Typography variant="body1">
        Welcome to the assignments page!
      </Typography>
      <Button component={Link} to="/" variant="outlined" sx={{ mt: 2 }}>
        Back to Home
      </Button>
    </div>
  );
}

export default Assignments;