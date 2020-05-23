import React from "react";
import {Link} from "react-router-dom";

class NavLink extends React.Component {
    render() {
        return <Link className="nav-link" to={this.props.to}>{this.props.text}</Link>;
    }
}

export default NavLink