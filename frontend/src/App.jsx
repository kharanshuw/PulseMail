import { useState } from 'react'
import './App.css'
import './index.css'
import EmailSender from './component/EmailSender'
import { useEffect } from 'react'

function App() {

  const [darkmode, setdarkmode] = useState(false);

  useEffect(
    () => {
      if (darkmode) {
        document.documentElement.classList.add('dark');
      }
      else {
        document.documentElement.classList.remove('dark');
      }
    }, [darkmode]
  )



  const toggleDarkMode = () => {
    setdarkmode(
      prevMode => !prevMode
    )
  }


  return (
    <>

      <div className='min-h-screen bg-gray-200 dark:bg-neutral-950 relative transition-colors duration-300'>

        <button
          onClick={toggleDarkMode}
          className='fixed top-3 lg:top-4 right-3 lg:right-4 w-9 h-9 lg:w-10 lg:h-10 flex justify-center items-center
        rounded-full bg-amber-500 text-neutral-950 shadow-lg hover:bg-amber-600 transition-colors'>


          <i className={`bx bx-${darkmode ? 'sun' : 'moon'} text-lg lg:text-xl`}>

          </i>
        </button>


        <EmailSender></EmailSender>
      </div>

    </>
  )
}

export default App
