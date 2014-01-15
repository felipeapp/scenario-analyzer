<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />

	<h2> <ufrn:subSistema /> &gt; Solicita��es de Bolsas REUNI de Assist�ncia ao Ensino </h2>

	<div class="descricaoOperacao">
		<p>
			<b> Caro Coordenador, </b>
		</p> 	
		<p>
			Est�o listadas aqui todas as solicita��es de bolsas REUNI cadastradas pelo seu Programa de P�s-Gradua��o.
			Atrav�s desta p�gina ser� poss�vel consultar os dados de uma solicita��o e acampanhar sua situa��o 
			dentro do processo e avalia��o das propostas pela Pr�-Reitoria de P�s-Gradua��o.
		</p>
	
	</div>

	<div class="infoAltRem" style="width:80%">
        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicita��o
	</div>

	<h:form id="form">
	<table class="listagem" style="width:80%">
		<caption> Solicita��es cadastradas </caption>
		<thead>
		<tr>
			<th> Edital </th>
			<th> N&ordm; de Bolsas Solicitadas </th>
			<th> Status </th>
			<th> </th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{solicitacaoBolsasReuniBean.all}" var="_solicitacao">
			<tr>
				<td> ${_solicitacao.edital.descricao} </td>
				<td align="center"> ${ fn:length(_solicitacao.planos) } </td>
				<td> ${ _solicitacao.descricaoStatus } </td>
				<td> 
					<h:commandButton image="/img/view.gif" title="Visualizar solicita��o" action="#{solicitacaoBolsasReuniBean.view}" styleClass="noborder" id="verSolicit">
						<f:setPropertyActionListener value="#{_solicitacao}" target="#{solicitacaoBolsasReuniBean.obj}"/>
					</h:commandButton>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	