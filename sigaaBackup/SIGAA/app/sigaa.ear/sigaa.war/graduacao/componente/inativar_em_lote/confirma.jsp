<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 

<h2><ufrn:subSistema /> > Inativar Componentes de Departamento</h2>
<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>Confirme a lista de componentes que serão inativados.</p>
</div>
<f:view>
<h:form id="form">
	<a4j:keepAlive beanName="inativarComponentesDepartamentoMBean" />
	<table class="formulario" width="85%">
		<caption>Componentes Curriculares do Departamento (${ fn:length(inativarComponentesDepartamentoMBean.resultadosBusca) })</caption>
		<thead>
			<tr>
				<th width="8%" style="text-align: center;">Código</th>
				<th>Nome</th>
				<th width="6%" style="text-align: right;">Carga Horária<br/>Total</th>
				<th width="25%">Tipo</th>
				<th style="text-align: center;" width="5%">Ativo</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${inativarComponentesDepartamentoMBean.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center;">${item.codigo}</td>
					<td>${item.detalhes.nome}</td>
					<td style="text-align: right;">${item.detalhes.chTotal} h</td>
					<td>
						<c:if test="${!item.atividade}">${item.tipoComponente.descricao}</c:if>
						<c:if test="${item.atividade}">
							${item.tipoAtividade.descricao}<br/>
							(${item.formaParticipacao.descricao})
						</c:if>
					</td>
					<td style="${ item.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${item.ativo}"/></td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="5">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<div style="text-align: left; width: 100%" >
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</div>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton action="#{inativarComponentesDepartamentoMBean.inativar}" value="Inativar Componentes Curriculares" id="inativar"/>
					<h:commandButton action="#{inativarComponentesDepartamentoMBean.formBusca}" value="<< Voltar" id="voltar"/>
					<h:commandButton action="#{inativarComponentesDepartamentoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

