//------------------------------------------------------------------------------
//  Skype Desktop API (Public API) Protocol Terminal Example
//
//  Tested with Delphi 2007 for Win32,
//  Skype v.4.1.0.104
//
//  To use the code in this example:
//  1. Create new VCL Forms application.
//  2. Drag & drop Standard TMemo component named Memo1.
//  3. Drag & drop Standard TEdit component named Edit1.
//  4. Copy and paste this example code into code editor.
//  5. Go back to Form view and link following events:
//    5.1 Form1.OnCreate -> FormCreate
//    5.2 Edit1.OnKeyPress -> Edit1KeyPress

Unit MainUnit;

Interface

Uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls;

Type
  TForm1 = Class(TForm)
    Memo1: TMemo;
    Edit1: TEdit;
    Procedure FormCreate(Sender: TObject);
    Procedure Edit1KeyPress(Sender: TObject; var Key: Char);
  Private
    MsgAttach         : LongWord;
    MsgDiscover       : LongWord;
    SkypeWindowHandle : HWND;
    Function  ProcessMessages (var Msg : TMessage) : Boolean;
    Procedure SendCommand (Cmd : String);
    Procedure RecvCommand (Cmd : String);
  End;

Var
  Form1: TForm1;

Implementation

{$R *.dfm}

//------------------------------------------------------------------------------
// Link this to your main form's OnCreate event.

Procedure TForm1.FormCreate(Sender: TObject);

Begin
  // UI prettyfication.
  Caption := 'Skype Public API Terminal';
  Memo1.Clear;
  Edit1.Clear;
  Memo1.Align       := alClient;
  Memo1.ReadOnly    := True;
  Memo1.ScrollBars  := ssVertical;
  Edit1.Align       := alBottom;
  ActiveControl     := Edit1;

  SkypeWindowHandle := 0;

  // Here we will get back message type IDs
  MsgAttach   := RegisterWindowMessage('SkypeControlAPIAttach');
  MsgDiscover := RegisterWindowMessage('SkypeControlAPIDiscover');

  // ProcessMessage will handle incoming messages from Skype client
  Application.HookMainWindow(ProcessMessages);

  // Broadcasting all over the system that this application wants to
  // attach itself to Skype public API. Response from Skype will be
  // handled in ProcessMessages.
  PostMessage(HWND_BROADCAST, MsgDiscover, Application.Handle, 0);
End;


//------------------------------------------------------------------------------
// Here we handle finalising the handshake and later, incoming notification
// messages from Skype.

Function TForm1.ProcessMessages(var Msg: TMessage) : Boolean;

Begin
  // MsgAttach sort of message. This we will get as resoponse from Skype
  // to MsgDiscover broadcast. WParam will contain Skype API window handle
  // (which is not necessarily Skype's UI main window handle). That handle
  // we can use to send further messages directly to Skype, instead of
  // broadcasting. LParam will contain current handshake status code.
  if (Msg.Msg = MsgAttach) Then
  Begin
    SkypeWindowHandle := Msg.WParam;
    Case Msg.LParam  of
      0 : Begin
            Memo1.Lines.Add('** Attach success');
            SendCommand('protocol 999');
            SendCommand('get skypeversion');
          End;
      1 : Memo1.Lines.Add('** Attach pending');
      2 : Memo1.Lines.Add('** Attach refused');
      3 : Memo1.Lines.Add('** Attach unavailable');
    End;
    Result := true;
    Exit;
  End;

  // Here we have recieved a notification message from Skype.
  if (Msg.Msg = WM_COPYDATA) and (HWND(Msg.WParam) = SkypeWindowHandle) Then
  Begin
    // The LParam contains a pointer to a TCopyDataStruct record.
    // lpData field of that record conatins pointer to a null-terminated string.
    // Through typecasting, we will pass that string to our RecvCommand procedure,
    // where further processing can take place.
    RecvCommand(PChar(PCopyDataStruct(Msg.LParam).lpData));

    // Setting the Msg.Result is important here, to notify Skype that the
    // message has been processed.
    Msg.Result := 1;
    Result := true;
    Exit;
  End;

  Result := False;
End;


//------------------------------------------------------------------------------
// Called from  ProcessMessages, when an incoming message from Skype is detected.
// This is where we could put parsing of those notification messages.

Procedure TForm1.RecvCommand(Cmd: string);

Begin
  Memo1.Lines.Add('-> ' + Cmd);
End;


//------------------------------------------------------------------------------
// Here we use Windows SendMessage to transmit API commands to Skype client.
// LParam contains pointer to CopyDataSruct record.
// cbData field of that record contains message length (+1 here because of
// the termianting null character). lpData field contains pointer to
// null-terminated string.

Procedure TForm1.SendCommand(Cmd: string);

Var CopyData : CopyDataStruct;
    StrBuff  : Array [0..1023] of Char;
    I        : Integer;

Begin
  if SkypeWindowHandle = 0 Then Exit;

  CopyData.dwData := 0;
  CopyData.cbData := Length(Cmd) + 1;
  CopyData.lpData := @StrBuff;

  for I := 1 to CopyData.cbData do StrBuff[I-1] := Cmd[I];
  StrBuff[CopyData.cbData] := #00;

  SendMessage(SkypeWindowHandle, WM_COPYDATA, Application.Handle, LPARAM(@CopyData));
End;

//------------------------------------------------------------------------------
// Link this to your edit field's OnKeyPress event.
// If Enter (char #13) key is pressed, send edit field content to Skype.

Procedure TForm1.Edit1KeyPress(Sender: TObject; var Key: Char);

Begin
  if Key = #13 then
  Begin
    Memo1.Lines.Add('<- ' + Edit1.Text);
    SendCommand(Edit1.Text);
    Edit1.Clear;
  End;
End;

End.

