syntax on
set mouse=a
set autoindent
set tabstop=2
"Backspace doesn't work properly for some reason in insert mode
" in Vim 8.0
set backspace=indent,start

function! MouseScroll()
  "mark b is the current cursor position
  "mark a is the previous cursor position
  norm mb
  let currPos=line('.')
  norm `a
  let prevPos=line('.')
  if currPos>prevPos
    norm `bma
    norm 
  elseif currPos<prevPos
    norm `bma
    norm 
  endif
endfunction

map <LeftDrag> ma<LeftMouse>:call MouseScroll()<cr>
imap <LeftDrag> <ESC>ma<LeftMouse>:call MouseScroll()<cr>:norm gi<cr>

"Nice function @DJMcMayhem wrote for me for golfing in V!
" that I modified a bit to support the different modes
function! Alt(mode)
  let c = nr2char(getchar() + 128)
  "c is command mode
  if a:mode=="c"
    return "".c
  else
  "gi is insert mode
    exec 'normal '.a:mode.c
  endif
endfunction

"Support for different modes: normal, visual, command-line, insert
nnoremap <C-i> :call Alt("")<cr>
vnoremap <C-i> :call Alt("")<cr>
cnoremap <expr> <C-i> Alt("c")
inoremap <C-i> <C-o>:call Alt("gi")<cr>

"V is super cool
source ~/golfing/V/nvim/motions.vim
source ~/golfing/V/nvim/normal_keys.vim
source ~/golfing/V/nvim/regex.vim
source ~/golfing/V/nvim/math.vim

function! Dec2BinB()
norm y$
if @">0
  norm k
  call Dec2BinB()
else
  norm 
endif
endfunction

function! Dec2BinA()
 norm 0may$
 if @"==0
   norm h`a
 else
   norm G
   call Dec2BinB()
   norm `a
   call Dec2BinA()
 endif
endfunction
"Decimal to Binary converter (trims leading zeroes)
function! Dec2Bin(n)
 norm ma
 let @a=a:n
 new
 norm "ap
 norm 8o0H
 call Dec2BinA()
 norm 9gJ
 s/^0*
 norm 0y$
 q!
 :norm `ap
endfunction
