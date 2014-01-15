<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="mensagemOrientacao" />


<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema/> &gt; Mensagens de Orientação Enviadas </h2>
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		
		<p>Nesta tela são listadas as mensagens enviadas para seus orientandos, ativos ou inativos.</p>
		<p>É possível, ainda, filtrar os dados mostrados selecionando um discente específico na listagem apresentada.</p>
	</div>
	<table class="formulario" width="50%">
	<h:form id="formBusca">
	<caption>Busca de Mensagens</caption>
	<tbody>
		<tr>
			<td>
				<input type="radio" id="checkDiscente" name="paramBusca" value="orientandos" class="noborder"
				${mensagemOrientacao.buscaOrientandos ? 'checked="checked"' : '' }>
			</td>
			<td><label for="checkDiscente">Discente:</label></td>
			<td>
				<h:selectOneMenu value="#{mensagemOrientacao.discente.id }" onchange="$('checkDiscente').checked = 'checked';">
					<f:selectItem itemLabel="-- SELECIONE UM DISCENTE --" itemValue="0" />
					<f:selectItems value="#{mensagemOrientacao.allOrientandosCombo }" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td>
				<input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"
				${mensagemOrientacao.buscaTodos ? 'checked="checked"' : '' }>
			</td>
			<td><label for="checkTodos">Todos</label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{mensagemOrientacao.buscar}" id="btnbscar"/>
				<h:commandButton value="<< Voltar" action="#{orientacaoAcademica.listar }" id="btnvoltar" rendered="#{mensagemOrientacao.permiteVoltar }"/>
				<h:commandButton value="Cancelar" action="#{mensagemOrientacao.cancelar}" id="btaoCancelOp"/>
			</td>
		</tr>
	</tfoot>
	</h:form>
	</table>

	<br/>
	<c:if test="${not empty mensagemOrientacao.mensagens}">
		<table class="listagem">
			<caption>Lista de Mensagens Enviadas</caption>
			<thead>
				<td style="text-align: center;">Matrícula</td>
				<td>Discente</td>
				<td style="text-align: center;">Data de envio</td>
			</thead>
			<tr>
				<c:forEach items="${mensagemOrientacao.mensagens}" var="mensagem" varStatus="status" >
				<c:set var="stripe" value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>
					<tr class="${stripe}">
						<td style="text-align: center;">
							${mensagem.orientacaoAcademica.discente.matricula }
						</td>
						<td width="80%">
							${mensagem.orientacaoAcademica.discente.pessoa.nome }
						</td>
						<td style="text-align: center;" nowrap="nowrap">
							<ufrn:format type="data" valor="${mensagem.dataCadastro }" />
						</td>
					</tr>
					<tr class="${stripe}">
						<td colspan="3" style="padding: 1% 3% 1% 3%;"><em>${mensagem.mensagem }</em></td>
					</tr>
				</c:forEach>
			</tr>
		</table>
		<h:form>
		<div style="text-align: center;"> 
		    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
		 
		    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" rendered="#{ paginacao.totalPaginas > 1 }">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		    </h:selectOneMenu>
		 
		    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		    <br/><br/>
		 
		    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
		</div>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>