
import '../Footer/index.css'

const FooterComponent = () => {
  return (
    <div>
       <footer className="footer">
      <div className="footer-content">
        <p>&copy; 2024 Your Company</p>
        <nav>
          <ul>
            <li><a href="#">Home</a></li>
            <li><a href="#">About</a></li>
            <li><a href="#">Services</a></li>
            <li><a href="#">Contact</a></li>
          </ul>
        </nav>
      </div>
    </footer>
    </div>
  )
}

export default FooterComponent