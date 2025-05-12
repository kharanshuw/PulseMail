import { useState } from 'react'
import './App.css'
import './index.css'
import EmailSender from './component/EmailSender'
import { useEffect } from 'react'

function App() {


  // ✅ Retrieve dark mode state from localStorage or default to false
  const [darkmode, setdarkmode] = useState(() => {
    const storedDarkMode = localStorage.getItem("darkmode") === "true";
    console.log("🔍 Initial Dark Mode State from localStorage:", storedDarkMode); // ✅ Debugging log
    return storedDarkMode;

  }
  );

  useEffect(
    () => {
      // ✅ Apply dark mode class based on state

      if (darkmode) {
        document.documentElement.classList.add('dark');

        console.log("🌑 Dark mode enabled"); // ✅ Debugging log

      }
      else {
        document.documentElement.classList.remove('dark');

        console.log("☀️ Light mode enabled"); // ✅ Debugging log

      }

      // ✅ Store the dark mode state in localStorage

      localStorage.setItem("darkmode", darkmode);

      console.log("💾 Dark mode state saved to localStorage:", darkmode); // ✅ Debugging log


    }, [darkmode]
  )


  /**
   * 🔄 Toggle Dark Mode
   * 
   * This function toggles dark mode state between true and false.
   */
  const toggleDarkMode = () => {
    setdarkmode(
      prevMode => !prevMode
    )
  }


  return (
    <>

      <div className='min-h-screen bg-gray-200 dark:bg-neutral-950 relative'>

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
