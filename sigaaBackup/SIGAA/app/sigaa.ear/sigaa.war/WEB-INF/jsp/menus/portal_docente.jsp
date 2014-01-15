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

<h2 class="title"> Portal do Docente </h2>

<div id="portal">
	<div id="painel_principal">

	<table width="100%">
	<tr>
		<td align="center"> <b>Comunicados</b> </td>
	</tr>
	<tr>
		<td> -> Atenção: A data limite para consolidação de turma na graduação é 10/10/2006</td>
	</tr>
	</table>
	<br>

	<table width="100%" class="formulario" border="1" >
		<tr>
			<td align="center" width="33%"> <A href="#">Calendário Acadêmico </A> </td>
			<td align="center" width="33%"> <A href="#">Lançar Notas </A> </td>
			<td align="center" width="33%"> <A href="#">Diário de Classe</A> </td>
		</tr>
		<tr>
			<td align="center" > <A href="#">Planos de<br> Aula</A> </td>
			<td align="center"> <A href="#"> Disciplina Virtual </A></td>
			<td align="center"> <A href="#">Caixa Postal</A></td>
		</tr>
	</table>
	<BR>
		<table class="formulario" width="100%">
			<caption class="listagem"> Disciplinas em 2006.2 </caption>
			<thead>
				<td> Nome </td>
				<td> Matriculados </td>
				<td> Notas </td>
			</thead>
			<tr>
				<td> POLÍTICA PÚBLICA I </td>
				<td align="center"> 20 </td>
				<td> fig </td>
			</tr>
			<tr>
				<td> GASTOS EM CAMPANHA II </td>
				<td align="center"> 200 </td>
				<td> fig </td>
			</tr>
		</table>



	</div>

	<div class="painel_menu esquerdo">
		<h3>Ensino</h3>
		<ul>
			<li class="nested">Disciplinas
			<ul>
				<li><a href="#"> Consultar </a></li>
			</ul>
			</li>
			<li class="nested">Turmas
			<ul>
				<li> <a href="#"> Consultar </a></li>
				<li> <a href="#"> Consolidar </a></li>
			</ul>
			</li>
			<li class="nested">Discentes
			<ul>
				<li> <a href="#">Consultar Orientandos </a></li>
				<li> <a href="#">Consultar Bolsistas </a></li>
				<li><html:link action="/pesquisa/cadastroRelatorioDiscenteParcial?dispatch=list&emitirparecer=true">Emitir Parecer de Relatórios Parciais</html:link></li>
				<li><html:link action="/pesquisa/cadastroRelatorioDiscenteFinal?dispatch=list&emitirparecer=true">Emitir Parecer de Relatórios Finais</html:link></li>
			</ul>
			</li>
		</ul>

		<h3>Pesquisa</h3>
		<ul>
			<li class="nested">Projetos
			<ul>
				<li><html:link action="pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true">Submeter Proposta de Projeto Interno</html:link></li>
				<li><html:link action="pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular">Cadastrar Projeto Externo</html:link></li>
		        <li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=list">Buscar Projetos de Pesquisa</html:link></li>
				<li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=listByCoordenador">Renovar Projeto de Pesquisa</html:link></li>
			</ul>
			</li>
			<li class="nested">Bolsas
			<ul>
				<li><html:link action="#">Solicitar Cotas</html:link></li>
				<li><html:link action="/pesquisa/indicarBolsista?dispatch=popular">Indicar/Substituir/Remover Bolsista</html:link></li>
			</ul>
			</li>

			<li class="nested">Grupos de Pesquisa (Bases)
			<ul>
				<li><html:link action="/pesquisa/cadastroGrupoPesquisa.do?dispatch=list">Consultar Grupos de Pesquisa</html:link></li>
				<li><html:link action="/pesquisa/cadastroLinhaPesquisa.do?dispatch=list">Alterar/Remover Linhas de Pesquisa</html:link></li>
			</ul>
			</li>

			<li class="nested">Áreas de Conhecimento do CNPQ
			<ul>
    			<li><html:link action="/pesquisa/cadastroAreaConhecimento?dispatch=list">Listar</html:link></li>
			</ul>
			</li>


			<li class="nested">Relatórios
			<ul>
				<li><html:link action="/pesquisa/cadastroRelatorioProjeto?dispatch=edit">Submeter Relatório de Projeto</html:link></li>
				<li><html:link action="/pesquisa/cadastroRelatorioProjeto?dispatch=list">Consultar/Alterar Relatórios do Projeto</html:link></li>

                <li><html:link action="/pesquisa/cadastroRelatorioDiscenteParcial?dispatch=list&emitirparecer=true">Emitir Parecer de Relatórios Parciais</html:link></li>
                <li><html:link action="/pesquisa/cadastroRelatorioDiscenteFinal?dispatch=list&emitirparecer=true">Emitir Parecer de Relatórios Finais</html:link></li>
			</ul>
			</li>
			<li>Consultar Editais</li>
		</ul>
	</div>

	<div class="painel_menu direito">

		<h3>Extensão</h3>
		<ul>
			<li class="nested">Atividades de Extensão
			<ul>
				<li> <a href="#">Cadastrar </a></li>
				<li> <a href="#">Alterar </a></li>
				<li> <a href="#">Consultar </a></li>
				<li> <a href="#">Verificar Andamento da Avaliação </a></li>
				<li> <a href="#">Alterar Coordenador </a></li>
				<li> <a href="#">Renovar </a></li>
				<li> <a href="#">Avaliar </a></li>
			</ul>
			</li>
			<li class="nested">Discentes
			<ul>
				<li>Cadastrar</li>
				<li>Alterar</li>
				<li>Consultar</li>
			</ul>
			</li>
			<li class="nested">Submissão de Relatórios
			<ul>
				<li>Coordenador</li>
				<li>Bolsista</li>
			</ul>
			</li>
			<li>Editais</li>
		</ul>

		<h3>Monitoria</h3>
		<ul>
			<li class="nested">Projetos
			<ul>
				<li> <a href="${ctx}/monitoria/ProjetoMonitoria/form.jsf">Cadastrar </a></li>
				<li> <a href="${ctx}/monitoria/ProjetoMonitoria/lista.jsf">Consultar </a></li>
				<li> <a href="#">Renovar </a></li>
				<li>Solicitar Reconsideração</li>
				<li>Avaliar Pedido de Renovação</li>
				<li>Avaliar Projeto</li>
			</ul>
			</li>
			<li>Monitores
			<ul>
				<li><a href="${ctx}/monitoria/SelecaoMonitor/lista.jsf">Cadastrar Resultado da Seleção</a></li>
				<li>Alterar Monitor</li>
				<li>Distribuir Monitores</li>
			</ul>
			</li>
			<li>Relatórios
			<ul>
				<li>Cadastrar</li>
				<li>Alterar</li>
				<li>Avaliar</li>
			</ul>
			</li>
			<li class="nested">Resumos (SID)
			<ul>
				<li><a href="${ctx}/monitoria/ResumoSid/form.jsf">Cadastrar</a></li>
				<li><a href="${ctx}/monitoria/ResumoSid/lista.jsf">Alterar</a></li>
				<li><a href="${ctx}/monitoria/ResumoSid/avaliar.jsf">Avaliar</a></li>
			</ul>
			</li>
			<li>Editais</li>
		</ul>
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