syntax on
set mouse=a
set autoindent

function! Alt()
	  let c = nr2char(getchar() + 128)
	  exec 'normal gi'.c
		endfunction
inoremap <C-i> <C-o>:call Alt()<cr>

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
