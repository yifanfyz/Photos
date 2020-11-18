# Photos
A photo management Application designed by JavaFX

------------------------------------------------------------------------------------------------------------------------------------------------------------
For the page of "login",
ADMIN LOG IN: You can enter "admin" and click "login" button.
STOCK LOG IN: You can enter "stock" and click "login" button.
QUIT:         You can just click "QUIT" button to quit the app.

------------------------------------------------------------------------------------------------------------------------------------------------------------
For the page of "admin",
ADD USER:     You can enter a name, then click "ADD" button. If the user is existing, the app will show the alert.
DELETE USER:  You can select an existing user, then click "DELETE" button.
LOG OUT:      You can just click "LOGOUT" button.
QUIT:         You can just click "QUIT" button to quit the app.

------------------------------------------------------------------------------------------------------------------------------------------------------------
For the page of "user",
We sort features by their properties and put them in a toolbar. And a seperate field for Searching. (at the right of the app window)

Album    -> open, create, delete, rename
Photos   -> copy, paste, move, add, delete
Property -> caption, add tag, delete tag

1. Features of Album,
OPEN:   Select an existing album, and click "OPEN". If there is no existing album, you need to create a new album first. Otherwise, the app will show the alert.
CREATE: Enter a name and click "CREATE". If there is an existing album with same name, the app will show the alert. 
DELETE: Select an existing album, and click "DELETE". 
RENAME: Select amn existing album, and click "RENAME", then enter a name. If there is an existing album with same name, the app will show the alert. 

2. Features of Photos,
WARNING: Only after you open an valid album, you can do the features of photos. Otherwise, all the features of photos are not availabe. 
COPY:   Select a photo, and click "COPY" 
PASTE:  Once you copied a valid photo, select a different album that you want to paste to, then click the "PASTE".
MOVE:   Once you copied a valid photo, select a different album that you want to move to, then click the "MOVE".
ADD:    Click the "ADD", then you can add a photo file from your local computer.
DELETE: Select a valid photo, then click "DELETE".

3. Features of Property,
WARNING:    Only after you select a valid photo, you can make changes on the property of this photo. 
CAPTION:    Click the "CAPTION", then you can caption/recaption.
ADD TAG:    You can enter a tag with tag type, and tag value in the textFidle in the middle of the window, and then you must to select exactly one checkbox with                 single/multiple. Then click "ADD TAG".
DELETE TAG: You can select a existing tag and click "DELETE". 

4. Features of Searching,
SEARCHING BY DATE: Select the range of date that you want to search, and then click "Search" just under the DatePicker.
SEARCHING BY TAG:  Enter the tag you want to searched by, and click "Search" just under the textField. If you want to search two tags with conjunction or        
                   disjunction, you have to choose exactly one of "and"/"or".
CREAT ALBUM:       If you want to create an album with the searching result, click the "CREATE ALBUM" button, and enter a name. 

5. Other Features,
SLIDESHOW: Click "<" or ">" to display like a slide show.
LOGOUT: Click "LOGOUT" to logout the current user. The app will not close if you want to login again.
QUIT: Click "QUIT" to shut down the app.

------------------------------------------------------------------------------------------------------------------------------------------------------------












