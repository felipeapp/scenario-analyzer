<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<!--[if IE]><![if lt IE 7]>
<style type="text/css">
#portal .painel_menu ul ul {
	display: none;
	position: absolute;
	top: 2px;
	left: 195px;
	width: 250px;
	background: #FEFEFE;
	border-width: 1px 1px 1px 0;
}

#portal .direito ul ul{
	position: absolute;
	left: -285px;
	border-width: 1px 0 1px 1px;
}

</style>
<![endif]><![endif]-->

<h2 class="title"> Portal do Discente </h2>

<div id="portal">
	<div class="painel_menu esquerdo">
		<h3>Ensino</h3>
		<ul>
			<li> <a href="#"> Consultar Turma </a></li>
			<li> <a href="#"> Consultar Disciplina</a></li>
			<li> <a href="#"> Atestado de Matrícula </a></li>
			<li> <a href="#"> Histórico </a></li>
		</ul>

		<h3>Pesquisa</h3>
		<ul>
			<li class="nested">Plano de Trabalho
			<ul>
				<li><html:link action="/pesquisa/planoTrabalho/wizard.do?dispatch=list">Consultar</html:link></li>
			</ul>
			</li>
			<li class="nested">Relatórios
			<ul>
				<li> <html:link action="/pesquisa/cadastroRelatorioDiscenteParcial?dispatch=edit"> Enviar Relatório Parcial </html:link> </li>
				<li> <html:link action="/pesquisa/cadastroRelatorioDiscenteFinal?dispatch=edit"> Enviar Relatório Final </html:link></li>
				<li> <html:link action="/pesquisa/cadastroRelatorioDiscenteParcial?dispatch=list">Consultar/Alterar Relatorio Parcial</html:link></li>
				<li> <html:link action="/pesquisa/cadastroRelatorioDiscenteFinal?dispatch=list"> Consultar/Alterar Relatório Final </html:link></li>

			</ul>
			</li>
		</ul>
	</div>

	<div class="painel_menu direito">
		<h3>Extensão</h3>
		<ul>
			<li class="nested">Plano de Trabalho
			<ul>
				<li> <a href="#">Consultar </a></li>
			</ul>
			</li>
			<li class="nested">Relatórios
			<ul>
				<li> <a href="#"> Enviar</a></li>
				<li> <a href="#"> Consultar </a></li>
				<li> <a href="#"> Alterar </a></li>
			</ul>
			</li>
		</ul>

		<h3>Monitoria</h3>
		<ul>
			<li class="nested">Plano de Trabalho
			<ul>
				<li> <a href="#">Cadastrar </a></li>
				<li> <a href="#">Consultar </a></li>
			</ul>
			</li>
			<li class="nested">Relatórios
			<ul>
				<li> <a href="#"> Enviar</a></li>
				<li> <a href="#"> Consultar </a></li>
				<li> <a href="#"> Alterar </a></li>
			</ul>
			</li>
			<li class="nested">Freqüência
			<ul>
				<li> <a href="#"> Consultar </a></li>
			</ul>
			</li>
		</ul>
	</div>

	<div id="painel_principal">

	<table width="100%" class="listagem">
		<tr>
			<td align="center" width="25%"> Calendário Acadêmico </td>
			<td align="center" width="25%"> Consultar Notas </td>
			<td align="center" width="25%"> Turmas Disponíveis </td>
			<td align="center" width="25%"> Caixa Postal</td>
		</tr>
	</table>

	<br>
	<table class="listagem" width="100%">
		<caption> Disciplinas em 2006.2 </caption>
		<thead>
			<tr>
			<td> Nome   </td>
			<td> Nota 1 </td>
			<td> Nota 2 </td>
			<td> Média </td>
			</tr>
		</thead>
		<tr>
			<td> POLÍTICA PÚBLICA I </td>
			<td align="center"> 9,5 </td>
			<td align="center"> 8,3 </td>
			<td align="center"> NL </td>
		</tr>
		<tr>
			<td> GASTOS EM CAMPANHA II </td>
			<td align="center"> 9,5 </td>
			<td align="center"> 8,3 </td>
			<td align="center"> NL </td>

		</tr>
	</table>



	</div>

</div>

<script>
	startList = function() {
		if (document.all&&document.getElementById) {
			var navRoot = $("portal");
			var lis = navRoot.getElementsByTagName("LI");
			for (i=0; i<lis.length; i++) {
				node = lis[i];
				if (node.nodeName=="LI") {
					node.onmouseover=function() {
						this.className+=" over";
					}
					node.onmouseout=function() {
						this.className=this.className.replace(" over", "");
	   				}
				}
			}
	 	}
	}

	startList();
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>