import React from 'react';
import {useState} from 'react';

function EmailSender() {

    const [emaildata, setemaildata] = useState({
        to: "",
        subject: "",
        message: "",
    });

    function handlefieldchange(event, name) {
        setemaildata({
            ...emaildata, [name]: event.target.value
        });
    }


    function handlesubmit(event) {
        event.preventDefault();

        if (emaildata.to == "" || emaildata.message == "" || emaildata.subject == "" ) {

                Swal.fire({
                    title: "Invalid fields?",
                    text: "Invalid input's",
                    icon: "error"
                });

            return;
        }

        console.log(emaildata);
    }


    return (

        // Main Container
        <div className='min-h-screen w-full border-2 flex justify-center items-center '>


            {/* Email Card Wrapper */}
            <div className='email_card md:w-1/3 w-full border shadow p-4 sm:mx-4 md:mx-0 bg-white dark:bg-gray-700'>


                <h1 className='text-gray-900 text-3xl dark:text-white capitalize'>
                    email_card
                </h1>

                <p className='text-gray-700 dark:text-gray-100 capitalize'>
                    send email using your own app...
                </p>


                {/* form start */}

                <form onSubmit={handlesubmit}>

                    {/* Email Input Field */}
                    <div className="mb-5">


                        <label htmlFor="base-input"
                               className="capitalize block mb-2 text-sm font-medium text-gray-900 dark:text-neutral-50">

                            to

                        </label>


                        <input
                            value={emaildata.to}
                            onChange={(e) => handlefieldchange(e, "to")}
                            type="text" id="base-input" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg
             focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700
             dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500
             dark:focus:border-blue-500"
                            placeholder='enter email'
                        />


                    </div>


                    {/* Subject Input Field */}

                    <div className="mb-5">


                        <label htmlFor="base-input"
                               className="capitalize block mb-2 text-sm font-medium text-gray-900 dark:text-neutral-50">

                            subject

                        </label>


                        <input
                            value={emaildata.subject}

                            onChange={(e) => handlefieldchange(e, "subject")}
                            type="text" id="base-input" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg
                  focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700
                  dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500
                  dark:focus:border-blue-500"
                            placeholder='enter email'
                        />


                    </div>


                    {/* Message Textarea */}
                    <div className='mb-4'>

                        <label htmlFor="message"
                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Your
                            message</label>
                        <textarea
                            value={emaildata.message}
                            onChange={(e) => handlefieldchange(e, "message")}

                            id="message" rows="4"
                            className="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                            placeholder="Write your thoughts here..."></textarea>

                    </div>


                    {/* Action Buttons (Send & Clear) */}

                    <div className='button-container mb-4 flex justify-center gap-3'>


                        <button type="submit"
                                className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300
          font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 
          focus:outline-none dark:focus:ring-blue-800 capitalize">

                            send email

                        </button>


                        <button type="reset"
                                className="text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:ring-red-300
          font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 
          focus:outline-none dark:focus:ring-red-800 capitalize">

                            clear

                        </button>

                    </div>


                </form>

            </div>

        </div>
    )
}

export default EmailSender