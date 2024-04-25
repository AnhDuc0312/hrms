import '../Header/index.css'

const HeaderComponent = () => {
  return (
    <div>
      <header className="header">
      <div className="header-top">
        <div className="header-top-left">
        </div>
        <div className="header-top-right">
          <a href="#">Đăng nhập</a>
          <a href="#">Đăng ký</a>
        </div>
      </div>
      <div className="header-main">
        <div className="header-main-left">
         <img style={{}} width="100" height="100" src="https://img.icons8.com/bubbles/100/d.png" alt="d"/>
          <h1 style={{color: 'black'}}>Delight</h1>
        </div>
        <div className="header-main-right">
          <nav>
            <ul>
              <li><a href="#">TRANG CHỦ</a></li>
              <li><a href="#">TIN TỨC</a></li>
              <li><a href="#">PHÒNG BAN</a></li>
              <li><a href="#">LỊCH LÀM VIỆC</a></li>
              <li><a href="#">BIỂU MẪU</a></li>
              <li><a href="#">CHẤM CÔNG</a></li>
            </ul>
          </nav>
        </div>
      </div>
      <div className="header-bot">
      </div>
    </header>
    </div>
  )
}

export default HeaderComponent