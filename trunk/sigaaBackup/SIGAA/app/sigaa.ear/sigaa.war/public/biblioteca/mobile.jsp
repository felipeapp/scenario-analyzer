<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<style>
	#verTela {
		display: block;
		width: auto;
		min-height: 227px;
		margin: 15px auto;
	}

	#verTela.ultimo {
		margin: 15px auto 0;
	}

		#verTela .imgBloco {
			display: block;
			width: 174px;
			height: 212px;
			margin: 0px auto;
			padding-right: 4em;
		}

			.imgBloco img {
				display: block;
				width: 170px;
				height: 208px;
				border:2px solid #D99C44;
				margin-bottom: 3px;
			}

			.imgBloco span {
				display: block;
				width: auto;
				height: auto;
				text-align: center;
				font-size: 10px;
				color: #CC3333;
			}

	#verTelas {
		display: inline-block;
		*float:left;
		width: 100%;
		min-height: 227px;
		margin: 15px auto;
		text-align:center;
	}

	#verTelas.ultimo {
		margin: 15px auto 0;
	}

	#verTelas .imgBlocoMetade {
		display: inline-block;
		*float:left;
		width: 48%;
		height: 212px;
	}

		.imgBlocoMetade img {
			float:left;
			width: 170px;
			height: 208px;
			border:2px solid #D99C44;
			margin-bottom: 3px;
			text-align: right;
		}

		.imgBlocoMetade span {
			float:left;
			width: 174px;
			height: auto;
			text-align: center;
			font-size: 10px;
			color: #CC3333;
		}

		.imgBlocoMetade .esq {
			float:right;
		}

	#verTelas .dir {
		padding: 0 0 0 20px;
		*padding: 0 0 0 23px;
		text-align:left;
	}
</style>

<%-- Pagina PUBLICA que mostra os dados marc do T�tulo  --%>

<h2>  Biblioteca Mobile </h2>


<f:view>

	<div class="descricaoOperacao">
	
		<p><b>A Biblioteca Mobile</b> � um meio alternativo de acessar os servi�os da Biblioteca da ${ configSistema['siglaInstituicao'] } dando mais praticidade e comodidade aos usu�rios do sistema.
		<p>O SIGAA Mobile fornece seus servi�os para os discentes, docentes e funcion�rios da ${ configSistema['siglaInstituicao'] } e possibilita a visualiza��o, renova��o e pesquisa dos �ltimos
		 empr�stimos e consulta de t�tulos.</p>
		<p>Este m�dulo s� pode ser acessado por usu�rios da institui��o autenticados e consiste de 06 (seis) d�gitos que deve ser cadastrada atrav�s do SIGAA.</p>

		<div id="verTelas">
			<div class="imgBlocoMetade">
				<img class="esq" src="../images/biblioteca_mobile/login.jpg" title="Tela de acesso - Login"></img>
				<span class="esq">Tela de acesso - Login</span>
			</div>
			<div class="imgBlocoMetade dir">
				<img src="../images/biblioteca_mobile/menu_principal.jpg" title="Tela do menu principal"></img>
				<span>Tela do menu principal</span>
			</div>
		</div>

		<p>Atualmente os servi�os oferecidos s�o:</p>

		<ul class="listagem">
			<li>
				<b>Visualizar Empr�stimos</b><br/>
				Este servi�o lista todas as informa��es de cada material de empr�stimo feito pelo usu�rio logado na aplica��o: Data de Empr�stimo, Data de Renova��o, Prazo Devolu��o, C�digo de Barras, T�tulo e Autor.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/visualiza_emprestimos.jpg" title="Tela de visualiza��o de empr�stimos"></img>
						<span>Tela de visualiza��o de empr�stimos</span>
					</div>
				</div> 
			</li>
			<li>
				<b>Renovar Empr�stimos</b><br/>
				Nesta p�gina, s�o listados os empr�stimos disponibilizados para renova��o. Todo empr�stimo pode ser renovado uma vez, exceto empr�stimos de materiais especiais que possuem prazo de devolu��o 24h.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/renovar.jpg" title="Tela de renova��o de empr�stimos"></img>
						<span>Tela de renova��o de empr�stimos</span>
					</div>
				</div>  
			</li>
			<li>
				<b>�ltimos Empr�stimos</b><br/>
				A realiza��o da busca dos materiais emprestados necessita que o usu�rio informe um per�odo do empr�stimo. Esse per�odo n�o representa a data exata do empr�stimo e, sim, o per�odo em que os empr�stimos foram realizados.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/renovar.jpg" title="Tela de �ltimos empr�stimos"></img>
						<span>Tela de �ltimos empr�stimos</span>
					</div>
				</div> 
			</li>
			<li>
				<b>Consultar T�tulo</b><br/>
				Para que a consulta de um material seja realizada, o usu�rio deve informar o t�tulo e o autor desejado e, ent�o, selecionar a op��o Buscar.
				<div id="verTela" class="ultimo">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/consultar_titulo.jpg" title="Tela de consulta de t�tulos"></img>
						<span>Tela de consulta de t�tulos</span>
					</div>
				</div> 
			</li>
		</ul>
	</div>                
		
	

</f:view>


<%@include file="/public/include/rodape.jsp"%>