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

<%-- Pagina PUBLICA que mostra os dados marc do Título  --%>

<h2>  Biblioteca Mobile </h2>


<f:view>

	<div class="descricaoOperacao">
	
		<p><b>A Biblioteca Mobile</b> é um meio alternativo de acessar os serviços da Biblioteca da ${ configSistema['siglaInstituicao'] } dando mais praticidade e comodidade aos usuários do sistema.
		<p>O SIGAA Mobile fornece seus serviços para os discentes, docentes e funcionários da ${ configSistema['siglaInstituicao'] } e possibilita a visualização, renovação e pesquisa dos últimos
		 empréstimos e consulta de títulos.</p>
		<p>Este módulo só pode ser acessado por usuários da instituição autenticados e consiste de 06 (seis) dígitos que deve ser cadastrada através do SIGAA.</p>

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

		<p>Atualmente os serviços oferecidos são:</p>

		<ul class="listagem">
			<li>
				<b>Visualizar Empréstimos</b><br/>
				Este serviço lista todas as informações de cada material de empréstimo feito pelo usuário logado na aplicação: Data de Empréstimo, Data de Renovação, Prazo Devolução, Código de Barras, Título e Autor.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/visualiza_emprestimos.jpg" title="Tela de visualização de empréstimos"></img>
						<span>Tela de visualização de empréstimos</span>
					</div>
				</div> 
			</li>
			<li>
				<b>Renovar Empréstimos</b><br/>
				Nesta página, são listados os empréstimos disponibilizados para renovação. Todo empréstimo pode ser renovado uma vez, exceto empréstimos de materiais especiais que possuem prazo de devolução 24h.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/renovar.jpg" title="Tela de renovação de empréstimos"></img>
						<span>Tela de renovação de empréstimos</span>
					</div>
				</div>  
			</li>
			<li>
				<b>Últimos Empréstimos</b><br/>
				A realização da busca dos materiais emprestados necessita que o usuário informe um período do empréstimo. Esse período não representa a data exata do empréstimo e, sim, o período em que os empréstimos foram realizados.
				<div id="verTela">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/renovar.jpg" title="Tela de últimos empréstimos"></img>
						<span>Tela de últimos empréstimos</span>
					</div>
				</div> 
			</li>
			<li>
				<b>Consultar Título</b><br/>
				Para que a consulta de um material seja realizada, o usuário deve informar o título e o autor desejado e, então, selecionar a opção Buscar.
				<div id="verTela" class="ultimo">
					<div class="imgBloco">
						<img src="../images/biblioteca_mobile/consultar_titulo.jpg" title="Tela de consulta de títulos"></img>
						<span>Tela de consulta de títulos</span>
					</div>
				</div> 
			</li>
		</ul>
	</div>                
		
	

</f:view>


<%@include file="/public/include/rodape.jsp"%>