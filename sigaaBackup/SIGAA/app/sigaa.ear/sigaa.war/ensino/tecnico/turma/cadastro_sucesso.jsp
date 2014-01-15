<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
	div.menu-botoes li.buscar a p {
		background-image: url('/sigaa/img/buscar.gif');
	}
 
	div.menu-botoes li.home a p {
		background-image: url('/sigaa/img/home.gif');
	}
 
 	div.menu-botoes li.mesmo-componente a p {
		background-image: url('/sigaa/img/copia.png');
	}
	
	div.menu-botoes li.outro-componente a p {
		background-image: url('/sigaa/img/novo_documento.gif');
	}
</style>

<f:view>
<h:form id="menuTecnicoForm">
<h2><ufrn:subSistema/> > Cadastro de Turma</h2>

<div style="text-align: center;">
<div class="menu-botoes" style="display: inline-block; width: 730px">
	<ul class="menu-interno">
		<li class="botao-grande mesmo-componente">
			<h:commandLink action="#{turmaTecnicoBean.preCadastrarMesmoComponente}">
			  <h5>Nova Turma para o mesmo Componente</h5> 
			  <p>Cadastra uma nova turma para o mesmo componente curricular.</p>
			</h:commandLink>	
		</li>
 		<li class="botao-grande outro-componente">
			<h:commandLink action="#{turmaTecnicoBean.preCadastrarOutroComponente}">
			  <h5>Nova Turma para outro Componente</h5>
			  <p>Seleciona outro componente curricular para criar uma nova turma.</p> 
			</h:commandLink>	
		</li>
		<li class="botao-grande buscar">
			<h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}">
			  <h5>Consultar, Alterar ou Remover Turma</h5>
			  <p>Realiza uma busca geral por turmas, podendo alterar ou removê-las.</p> 
			</h:commandLink>	
		</li>
		<li class="botao-grande home">
			<h:commandLink action="#{turmaTecnicoBean.cancelar}">
			  <h5>Voltar ao Menu Principal</h5>
			  <p>Volta ao menu principal com todas as suas opções.</p> 
			</h:commandLink>
				
		</li>
	</ul>
</div>
</div>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	