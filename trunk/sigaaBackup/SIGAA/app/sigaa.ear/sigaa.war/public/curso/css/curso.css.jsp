<c:set var="_tpl" value="${portalPublicoCurso.detalhesSite.templateSite}"/>
<style type="text/css">
/* topo do site */
#corpo #topo_site {
	display: block;
	width: 100%;
	height: 142px;
}

	#topo_site #logo {
		display: inline-block;
		*float: left;
		width: 190px;
		height: 122px;
		text-align:center;
		vertical-align: top;
		padding: 10px;
	}

		#logo img {
			max-width: 190px;
			_width: 188px;
			max-height: 122px;
			_height: 120px;
		}

	#topo_site #nomes {
		display: inline-block;
		*float: left;
		width: 682px;
		height: 100%;
		margin: 0 0 0 10px;
		vertical-align: top;
	}

		#nomes .sigla {
			display: block;
			width: 100%;
			font-family: "Verdana", Arial, Helvetica, sans-serif;
			font-size: 20px;
			color: ${_tpl.corSigla};
			font-weight: bold;
			text-transform: uppercase;
			margin: 13px 0 10px;
		}

		#nomes .nome_curso {
			display: block;
			width: 100%;
			font-family: "Verdana", Arial, Helvetica, sans-serif;
			font-size: 16px;
			color: ${_tpl.corTitulo};
			text-transform: uppercase;
			line-height: 16px;
			margin: 24px 0 26px;
		}

			.nome_curso a {
				color: ${_tpl.corTitulo};
			}

			.nome_curso a:hover {
				text-decoration: underline;
			}

		#nomes .nome_centro {
			display: block;
			width: 100%;
			font-family: "Verdana", Arial, Helvetica, sans-serif;
			font-size: 13px;
			color: ${_tpl.corSubTitulo} !important;
			text-transform: uppercase;
			font-style: italic;
			margin-bottom: 5px;
		}

		#nomes .url_programa {
			display: block;
			width: 65%;
			float:left;
			font-family: "Verdana", Arial, Helvetica, sans-serif;
			font-size: 11px;
			color: #7d97fe;
		}

			.url_programa a {
				font-family: "Verdana", Arial, Helvetica, sans-serif;
				font-size: 11px;
				color: ${_tpl.corLink};
			}

			.url_programa a:hover {
				text-decoration: ${_tpl.corLinkHover};
			}
			
		#nomes .telefone {
			float: right;
			width: auto;
			height: auto;
			font-family: "Verdana", Arial, Helvetica, sans-serif;
			font-size: 13px;
			color: ${_tpl.corLink};
			font-style: italic;
			margin: -33px 0 0;
		}

			.telefone label {
				color: #000;
			}

		 
/* menu */
#corpo .menu {
	display: block;
	width: 100%;
	height: 26px;
	background-color: ${_tpl.corFundoMenu};
	border-bottom: ${_tpl.corFundoSubMenu} solid 1px;
}

	.menu .li-menu {
		float: left;
		padding: 0 10px 0 0;
		position: relative;
		border-left: ${_tpl.corFundoSubMenu} solid 1px;
		z-index: 10;
		height:27px;
	}

	.menu .primeiro {
		border-left: none;
	}

	.menu .item-menu {
		float: left;
		padding: 7px 0 0 10px;
		height: 20px;
		*height: 21px;
		color: ${_tpl.corTitMenu};
		font-weight: bold;
	}

	.menu span.item-menu {
		cursor: default;
	}

	.menu a:hover {
		text-decoration: underline;
	}

	.menu .sub-menu {
		display: none;
		padding: 10px 10px 5px 10px;
		position: absolute;
		top: 26px;
		left: 0;
		z-index: 11;
		background: ${_tpl.corFundoSubMenu};
	}

	.menu .sub-menu li {
		border-top: 1px solid ${_tpl.corFundoMenu};
		padding: 10px 0 5px 0;
	}

	.menu .sub-menu .primeiro {
		border: 0;
		padding: 0 0 5px 0;
	}

	.menu .sub-menu a {
		display: block;
		width: auto;
		color: ${_tpl.corTitMenu};
		white-space: nowrap;
	}

		.sub-menu a:hover {
			text-decoration: none;
		}

/* conteudo */
#corpo #conteudo {
	display: block;
	width:98%;
	min-height: 480px;
	_height: 480px;
	margin:20px 10px 0;
}

	.titulo {
		font-family: "Verdana", Arial, Helvetica, sans-serif;
		font-size: 16px;
		font-weight: bold;
		text-decoration: underline;
		color: ${_tpl.corTitCont};
	}

	h1, h2, h3, h4, h5, h6 {
		display: block;
		width: auto;
		height: auto;
		font-size: 100%;
		font-weight: bold;
		margin: 0 0 10px 0;
		color: ${_tpl.corSubTitulo};
	}

	.titulo_menor {
		font-family: "Verdana", Arial, Helvetica, sans-serif;
		font-size: 12px;
		font-weight: bold;
		text-decoration: underline;
		color: ${_tpl.corTitCont};
	}

	.leia_mais a {
		float: right;
		text-align: right;
		font-family: "Verdana", Arial, Helvetica, sans-serif;
		font-size: 10px;
		color: ${_tpl.corLink};
	}

	.saiba_mais a {
		float: left;
		text-align: left;
		font-family: "Verdana", Arial, Helvetica, sans-serif;
		font-size: 10px;
		color: ${_tpl.corLink};
	}
	
	.destaqueProcessoeletivo{
		font-weight: bold !important;
	}

	/* conteudo da coluna menu lateral */
	#conteudo #menu_lateral {
		display: inline-block;
		*float: left;
		width: 208px;
		height: auto;
		border: 1px solid #E1E1E1;
		vertical-align: top;
		margin: 3px 0 0;
	}

		#menu_lateral #titulo {
			display: block;
			padding: 20px 20px 0;
			color: ${_tpl.corTitCont};
			font-size: 14px;
			font-weight: bold;
			text-align: center;
		}

		#menu_lateral #links {
			display: block;
			padding: 20px 20px 15px;
		}

		#menu_lateral #links li {
			border-top: 1px solid #E1E1E1;
			padding: 10px 0 5px 0;
		}

		#menu_lateral #links .primeiro {
			border: 0;
			padding: 0 0 5px 0;
		}

		#menu_lateral #links a {
			display: block;
			width: auto;
			/*white-space: nowrap;*/
			color: ${_tpl.corLink};
		}

		#menu_lateral #links a:hover {
			color: #222222;
			text-decoration: none;
		}

	/* conteudo da coluna esquerda */
	#conteudo #esquerda {
		display: inline-block;
		*float: left;
		width: 72%;
		min-height: 513px;
		_height: 513px;
		margin: 0 22px 0 0;
		*margin: 0 24px 0 0;
	}

	#conteudo #esquerda.com_menu_lateral {
		width: 476px;
		*width: 480px;
		margin: 0 18px 0 20px;
		*margin: 0 20px 0;
	}
		#esquerda .informacoes {
			display: block;
			width: 96%;
			height: auto;
			margin: 10px 0 0 30px;
		}

			.informacoes p {
				line-height: 15px;
			}
			
			.informacoes .cor {
				color: ${_tpl.corTitCont};
			}
		
		#esquerda .topico {
			display: block;
			width: 100%;
			height: auto;
			font-weight: bold;
			color: ${_tpl.corTitCont};
			margin: 20px 0 0;
		}

			.topico strong {
				color: #000000;
			}

		/* noticia em destaque */
		#esquerda #destaque {
			display: block;
			width: 100%;
			height: 203px;
			padding:20px 0 0;
		}

			#destaque .d_titulo {
				display: block;
				width: 100%;
				height: 34px;
				font-weight: bold;
				font-size: 13px;
				margin: 0 0 10px 0;
			}

			#destaque .d_texto {
				display: block;
				width: 100%;
				height: 139px;
				line-height: 17px;
				text-align: justify;
				margin: 0 0 10px 0;
			}

				.d_texto .foto {
					float: left;
					margin: 0 10px 0 0;
					border: 1px solid #e9eef2;
				}

				.d_texto .data {
					float: left;
					width: auto;
					margin:5px 0 0;
					font-size: 9px;
					font-weight: bold;
				}
				
				.texto .topico2 {
					display: block;
					width: 100%;
					height: auto;
					color: #000000;
					margin: 20px 0 0;
				}
		
					.topico2 label {
						font-weight: bold;
						color: ${_tpl.corTitCont};
					}

		/* separador das noticias */
		#esquerda #linha {
			display: block;
			width: 100%;
			height: 1px;
			margin: 15px 0;
			_margin: 0px 0 10px;
			background: transparent url(../img/pontilhado.png) repeat-x left center;
		}

		/* ultimas noticias */
		#esquerda #listagem ul {
			display: block;
			width: 100%;
			height: auto;
			padding:20px 0 0;
			_padding:20px 0 4px;
		}

			#listagem ul li {
				display: block;
				width: 100%;
				margin: 0 0 8px 0;
			}

			#listagem ul li a:hover {
				text-decoration: underline;
			}

			#listagem ul li .data {
				font-size: 9px;
				font-weight: bold;
			}

	/* conteudo da coluna direita */
	#conteudo #direita {
		display: inline-block;
		*float: left;
		width: 249px;
		min-height: 513px;
		_height: 513px;
		vertical-align: top;
	}

		/* chamada */
		#direita #chamada {
			display: block;
			width: 100%;
			height: 227px;
			_height: 230px;
			margin: 18px 0 0;
		}

			#chamada #icone {
				float: right;
				width: 54px;
				height: 47px;
				position: relative;
				margin: -14px 0 0 0;
				_margin: -15px 0 0 0;
				z-index: 10px;
			}

			#chamada #caixa {
				float: left;
				width: 90%;
				height: 213px;
				_height: 216px;
				margin: -37px 0 0 0;
				_margin: -38px 0 0 0;
				padding: 8px;
				border: 1px solid #e1e1e1;
			}

				#caixa #titulo {
					display: block;
					width: 180px;
					height: auto;
					padding: 4px 0 13px;
					text-align: center;
				}

				#caixa .introducao {
					display: block;
					width: 100%;
					height: 35px;
					line-height: 18px;
					text-align: justify;
				}

				/* listagem referente ao processo seletivo */
				#caixa #listagem2 ul {
					display: block;
					width: 100%;
					height: 110px;
					padding: 15px 0 10px;
				}

					#listagem2 ul li {
						display: block;
						width: 100%;
						margin: 0 0 8px 0;
						font-style: italic;
						text-transform: uppercase;
					}

					#listagem2 ul li a:hover {
						text-decoration: underline;
					}

					#listagem2 ul li .data {
						text-transform: none;
					}

				/* listagem referente ao calendario */
				#caixa #listagem3 ul {
					display: block;
					width: 100%;
					height: 110px;
					padding: 15px 0 10px;
				}

					#listagem3 ul li {
						display: block;
						width: 100%;
						margin: 0 0 10px 0;
						font-size: 10px;
						font-weight: bold;
					}

					#listagem3 ul li .evento {
						display: block;
						width: 93%;
						height: auto;
						margin: 0 0 0 15px;
						font-size: 11px;
						font-style: italic;
						font-weight: normal;
					}

					#listagem3 ul li .evento a:hover  {
						text-decoration: underline;
					}

		/* separador das chamadas */
		#direita #espaco {
			display: block;
			width: 100%;
			height: 1px;
			margin: 20px 0;
			_margin: 15px 0;
		}


	/* ------------------------------ Conteudo das internas ------------------------------ */
	.texto {
		display: block;
		width: 100%;
		height: auto;
		margin: 20px 0;
		text-align: justify;
		line-height: 15px;
		color: ${_tpl.corTextoCont}
	}

		.texto .foto {
			float: left;
			margin: 2px 10px 5px 0;
			border: 1px solid #e9eef2;
		}
	
	.subtitulo {
		display: block;
		width: auto;
		height: auto;
		font-weight: bold;
		margin: 0 0 10px 0;
		color: ${_tpl.corSubTitulo};
	}

	.texto_afastado {
		display: block;
		width: 99%;
		height: auto;
		margin: 5px 0 0 10px;
	}

		.texto_afastado ul#listagem {
			display: block;
			width: 100%;
		}

			.texto_afastado ul#listagem li {
				display: block;
				width: 100%;
				margin: 15px 0 0 0;
				font-weight: bold;
				text-transform: uppercase;
			}

			.texto_afastado ul#listagem li.primeiro {
				margin: 0;
			}

			.texto_afastado ul#listagem li .dados {
				display: block;
				width: 98%;
				height: auto;
				margin: 6px 0 0 20px;
				font-weight: normal;
				text-transform: none;
			}

			.texto_afastado ul#listagem li .dados p {
				display: block;
				width: 100%;
			}

			.texto_afastado ul#listagem li .dados .cor {
				color: #7d97fe;
			}

			.texto_afastado ul#listagem li .data {
				color: #7d97fe;
			}

			.texto_afastado ul#listagem li a.cor {
				text-transform: none;
				color: #000;
			}

			.texto_afastado ul#listagem li a.cor:hover {
				text-decoration: underline;
			}
			
			
			/* legenda */
			.legenda {
				display: block;
				width: 98%;
				height: auto;
				border: 1px solid #e7e7e7;
				background: #F6F6F1;
				padding: 1%;
				margin: 20px auto;
				text-align: center;
			}
	
				.legenda img {
					margin: 0 2px;
					vertical-align: middle;
				}
		
				.legenda label {
					margin: 0 10px 0 1px;
					vertical-align: middle;
				}

	/* Detalhes da notícia */
	.data_cadastrada {
		display: block;
		width: 100%;
		height: auto;
		text-align: right;
		color: #7d97fe;
	}

		.data_cadastrada b {
			color: ${_tpl.corSubTitulo};
		}

	.arquivo_baixar {
		display: block;
		width: 100%;
		height: auto;
	}

		.arquivo_baixar a {
			color: #4D5D9C;
		}

		.arquivo_baixar a:hover {
			color: #222222;
		}

	/* listagem geral (tabela) */
	#listagem_tabela {
		display: block;
		width: 100%;
		height: auto;
		border: 1px solid #e7e7e7;
		padding: 0;
		margin: 20px 0 0;
	}

		#listagem_tabela #head_lt {
			display: block;
			width: 100%;
			padding: 15px 0;
			background:${_tpl.corFundoTabela} url(./img/pontilhado.png) repeat-x scroll left bottom;
			color: ${_tpl.corTituloTabela};
			font-size: 12px;
			font-weight: bold;
			text-align: center;
			text-transform: uppercase;
		}

		#listagem_tabela #group_lt {
			display: block;
			width: 99%;
			height: auto;
			padding: 5px;
			background: ${_tpl.corFundoTabela};
			color: ${_tpl.corTituloTabela};
			font-size: 11px;
			font-weight: bold;
			text-transform: uppercase;
		}

			#listagem_tabela #group_lt a {
				color: #FFF;
			}

			#listagem_tabela #group_lt a:hover {
				color: #FFF;
			}

		#listagem_tabela #table_lt, #table_lt tbody {
			display: block;
			width: 100%;
			height: auto;
		}

			#table_lt tbody tr td {
				padding: 5px;
				text-align: left;
				vertical-align: middle;
			}

			#table_lt tbody tr td.centro {
				text-align: center;
			}

			#table_lt tbody tr td.direita {
				text-align: right;
			}

			#table_lt tbody tr.campos {
				display: block;
				width: 100%;
				height: auto;
				*height: 18px;
				padding: 2px 0;
				background: ${_tpl.corSubCabTab};
				font-weight: bold;
			}
			#table_lt tbody tr.campos td {
				color: ${_tpl.corTitSubCabTab};
			}

			#table_lt tbody tr.linha_par {
				display: block;
				width: 100%;
				height: auto;
				*height: 18px;
				padding: 2px 0;
				background: #F9FBFD;
			}

			#table_lt tbody tr.linha_impar {
				display: block;
				width: 100%;
				height: auto;
				*height: 18px;
				padding: 2px 0;
				background: ${_tpl.corLinhaImpar};
			}

			#table_lt tbody tr td a.cor, #table_lt tbody tr td a.cor_ss {
				color: #4D5D9C;
			}

			#table_lt tbody tr td a.cor:hover {
				text-decoration: underline;
			}

			#table_lt tbody tr td a.cor_ss:hover {
				color: #222222;
			}

			#table_lt tbody tr td a.negrito:hover {
				font-weight: bold;
			}

			#table_lt tbody tr td a.italico:hover {
				font-style: italic;
			}

			#table_lt tbody tr td .topicos {
				display: block;
				width: auto;
				margin-left: 20px;
			}

				#table_lt tbody tr td .topicos span {
					display: block;
					width: auto;
					margin: 8px 0 0 0;
				}

				#table_lt tbody tr td .topicos a {
					display: block;
					width: auto;
					margin: 8px 0 0 0;
				}

			#table_lt tbody tr td #descricao {
				display: block;
				width: auto;
				margin-left: 20px;
				margin-right: 20px;
			}

				#table_lt tbody tr td #descricao span {
					display: block;
					width: auto;
					margin: 0 0 5px 0;
				}

				#table_lt tbody tr td #descricao span.negrito {
					font-weight: bold;
				}

				#table_lt tbody tr td #descricao span.links {
					margin: 10px 0 0 0;
					*margin: 10px 0 -5px 0;
				}

					#table_lt tbody tr td #descricao span.links a {
						float: left;
						width: auto;
						height: auto;
						margin: 0 15px 0 0;
					}

		#listagem_tabela #foot_lt {
			display: block;
			width: 100%;
			height: auto;
			padding: 5px 0;
			background: #4d5d9c;
			color: #FFFFFF;
			font-size: 11px;
			font-weight: bold;
			text-align: center;
		}

	#listagem_tabela.espaco_menor {
		margin: 10px 0 0;
	}

	/* formulario para busca */
	#caixa_formulario {
		display: block;
		width: 516px;
		height: 165px;
		_height: 165px;
		margin: 20px auto;
	}

		#caixa_formulario #icon_cf {
			float: right;
			width: 85px;
			height: 74px;
			position: relative;
			margin: -16px 0 0 0;
			z-index: 10px;
		}

		#caixa_formulario #formulario {
			float: left;
			width: 97%;
			height: auto;
			margin: -58px 0 0 0;
			border: 1px solid #e7e7e7;
		}
			#formulario #head_f {
				display: block;
				width: 100%;
				height: auto;
				padding: 15px 0;
				background: ${_tpl.corFundoTabela};
				color: ${_tpl.corTituloTabela};
				font-size: 12px;
				font-weight: bold;
				text-align: center;
				text-transform: uppercase;
			}

			#formulario #body_f {
				display: block;
				width: 450px;
				*width: 451px;
				height: auto;
				padding: 25px;
				text-align: center;
			}

				#formulario #body_f .campo {
					display: block;
					width: 196px;
					*width: 190px;
					_width: 175px;
					_height: auto;
					margin: 0px auto 10px;
				}

					#formulario #body_f .campo label  {
						float: left;
						font-weight: bold;
						color: #000000;
						padding: 3px;
						display:block;
					}
					
					#formulario #body_f .campo_maior label  {
						float: left;
						font-weight: bold;
						color: #000000;
						padding: 3px;
						display:block;
						min-width: 60px; 
					}
					
					#formulario #body_f .campo_maior label input {
						margin-right: 10px;
					}

				#formulario #body_f input {
					border: 1px #c8c8c6 solid;
					font-size: 11px;
					color: #222222;
					font-family: "Tahoma";
					padding: 2px 4px;
					height: 14px;
					margin: 0px;
					vertical-align: top;
				}
				
				#formulario #body_f .campo_maior {
					display: block;
					width: 360px;
					_height: auto;
					margin: 0px auto 10px;
					text-align:left;
				}
				

				#formulario #body_f select {
					border: 1px #c8c8c6 solid;
					font-size: 11px;
					color: #222222;
					font-family: "Tahoma";
					padding: 2px;
					width: 37px;
					height: 20px;
					margin: 0px;
					vertical-align: top;
				}
				
				#formulario #body_f .botoes {
					display: block;
					width: auto;
					height: auto;
					margin: 0px auto;
				}

				#formulario #body_f .bt_buscar {
						margin: 0px 3px 0 0;
						background: transparent url(./img/bt_buscar.jpg) no-repeat;
						border: none;
						cursor:pointer;
						height:25px;
						width:56px;
						vertical-align: top;
					}

					#formulario #body_f .bt_cancelar {
						margin: 0px;
						background: transparent url(./img/bt_cancelar.jpg) no-repeat;
						border: none;
						cursor:pointer;
						height:25px;
						width:56px;
						vertical-align: top;
					}

	/* ------------------------------ Conteudo das internas ------------------------------ */
	
	

	/* Erros */
	
	#painel-erros {
		background: #F9FBFD;
		padding: 3px 28px;
		border-bottom: 1px solid #C8D5EC;
	}
	
	#painel-erros ul{
		margin: 0;
		padding: 0;
		padding: 10px 0 10px 55px;
	}
	
	#painel-erros li{
		font-weight: bold;
		list-style-type: disc;
		margin-top: 5px;
	}
	
	#painel-erros ul.info {background: url(/shared/img/mensagens/info.gif) no-repeat left center;}
	#painel-erros ul.warning {background: url(/shared/img/mensagens/warning.gif) no-repeat left center;}
	#painel-erros ul.erros {background: url(/shared/img/mensagens/error.gif) no-repeat left center;}
	
	#painel-erros ul.info li {color: #0CA700;}
	#painel-erros ul.warning li {color: #333;}
	#painel-erros ul.erros li {color: #F11;}
	
	
 </style>