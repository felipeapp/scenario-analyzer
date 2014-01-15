<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>

	<a4j:keepAlive beanName="bolsaCnpqStrictoBean" />
	
	<h2><ufrn:subSistema /> &gt; Bolsistas CNPq</h2>

	<div class="descricaoOperacao">
		<h4> Caro usuário, </h4>
		<p>
			Esta operação destina-se a gerenciar o registro dos alunos bolsistas do CNPq na base de dados do sistema.
		</p>
	</div>

	<h:form>
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{bolsaCnpqStrictoBean.iniciarCadastro}" value="Cadastrar novo bolsista"/>
		<f:verbatim><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar dados</f:verbatim>
		<h:graphicImage value="/img/del_cal.png" style="overflow: visible;"/>: Finalizar bolsista <br />
	</div>
	</h:form>

	<h:form id="formListagem">
	
	<c:if test="${not empty bolsaCnpqStrictoBean.bolsistas}">
	<table class=listagem>
		<caption class="listagem">Bolsistas CNPq Ativos</caption>
		<thead>
			<tr>
				<td style="text-align:center;">Matrícula</td>
				<td>Nome</td>
				<td style="text-align:center;">Período da Bolsa</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="#{bolsaCnpqStrictoBean.bolsistas}" var="_bolsista" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td style="text-align:center;">${_bolsista.discente.matricula}</td>
				<td>${_bolsista.discente.nome}</td>
				<td style="text-align:center;"> 
					<ufrn:format type="data" valor="${_bolsista.dataInicio}" /> a <ufrn:format type="data" valor="${_bolsista.dataFim}" />
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder" title="Alterar dados" action="#{bolsaCnpqStrictoBean.iniciarCadastro}" id="linkAlterarDados">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:setPropertyActionListener target="#{bolsaCnpqStrictoBean.obj}" value="#{_bolsista}"/>
					</h:commandLink>
				</td>
				<td width="20">
					<h:commandLink styleClass="noborder" title="Finalizar bolsista" action="#{bolsaCnpqStrictoBean.finalizar}" 
						onclick="return confirm('Confirma a finalização da bolsa selecionada?');" id="linkFinalizarBolsista">
						<h:graphicImage url="/img/del_cal.png"/>
						<f:setPropertyActionListener target="#{bolsaCnpqStrictoBean.obj}" value="#{_bolsista}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
	</c:if>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
