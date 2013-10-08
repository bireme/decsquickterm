<%@page contentType="text/html"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>DeCSQuickTermService</title>
        <style>
            BODY, TEXTAREA, SELECT {
            font-family:  Arial, Verdana;
            font-size: 80%;   
            }            
            FORM{
            padding: 7px;
            }
            
        </style>
    </head>
    <body>
        <h1>DeCSQuickTermService</h1>
        
        <form action="search" method="get">
        
            idioma de pesquisa
            <select name="lang">
                <option value="pt">português</option>
                <option value="es">español</option>
                <option value="en">english</option>                        
            </select>
        
            palavra a consultar
            <input type="text" name="query" size="15">
            
            quantidade de termos
            <input type="text" name="count" size="2" value="20">
            <input type="submit" value="ok">
                
        </form>          
        
        
    </body>
</html>
