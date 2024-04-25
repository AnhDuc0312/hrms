
import '../Login/index.css'

const LoginComponent = () => {
    
  return (
    
    <div className="body">
      <div className="login-container">
        <h2>Login</h2>
        <form action="">
            <div className="input-group">
                <label htmlFor="username">Username:</label>
                <input type="text" id="username" name="username" placeholder="Enter your username" required/>
            </div>
            <div className="input-group">
                <label htmlFor="password">Password:</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required/>
            </div>
            
            <a href="">Forget password ! Reset</a>
           
            
            <button type="submit">Login</button>
        </form>
    </div>
    </div>
  )
}

export default LoginComponent