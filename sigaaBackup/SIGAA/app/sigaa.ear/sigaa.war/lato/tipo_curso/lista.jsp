<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.dominio.ModalidadeEducacao"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Tipo Curso de Lato</h2>

<h:form id="form">

	<div class="infoAltRem">
		
		<h:commandLink 	action="#{tipoCursoLatoMBean.preCadastrar}">
				<h:graphicImage url="/img/adicionar.gif" /> Cadastrar Tipo de Curso
		</h:commandLink>
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Tipo de Curso 
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Curso 	
	</div>

  <table class=formulario width="100%">
   <caption class="listagem">Tipo Curso Lato Sensu</caption>
		<thead>
			<tr>
				<th>Descrição</th>
				<th style="text-align: right;">Carga Horária Mínima</th>
				<th style="text-align: right;">Carga Horária Máxima</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		
		<c:forEach var="linha" items="#{tipoCursoLatoMBean.allAtivos}">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${linha.descricao}</td>
				<td style="text-align: right;">${linha.chMinima}</td>
				<td style="text-align: right;">${linha.chMaxima}</td>
				
				<td width="20">
					<h:commandLink action="#{tipoCursoLatoMBean.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>						
				
				<td width="20">
					<h:commandLink action="#{tipoCursoLatoMBean.inativar}" onclick="#{confirmDelete}" >
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{linha.id}"/>
					</h:commandLink>
				</td>						
				
			</tr>
		</c:forEach>
   </table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>