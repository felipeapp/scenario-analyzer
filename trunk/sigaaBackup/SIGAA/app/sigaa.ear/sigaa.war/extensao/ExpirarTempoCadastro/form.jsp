<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Expirar A��es com Cadastro em Andamento</h2>
	
	<a4j:keepAlive beanName="expirarTempoCadastro" />
	<h:form id="form">
		<div class="descricaoOperacao">
			<p align="center">
				Esta opera��o permite encerrar as A��es de Extens�o que possuem situa��o CADASTRO EM ANDAMENTO por determinados dias. 
				O docente pode reativar as a��es encerradas atrav�s da op��o 'A��es com Tempo de Cadastro Expirado' dispon�vel no Portal do Docente. 
			</p>
		</div>
		
		<table class="formulario" width="50%" >
			<caption class="listagem">Tempo de cadastro em andamento</caption>
			
			<tr>
				<th class="required">A��es com cadastro em andamento a mais de </th>
				<td>
					<h:inputText value="#{expirarTempoCadastro.diasEncerrar}"
					size="3" readonly="#{expirarTempoCadastro.readOnly}" onkeyup="return(formatarInteiro(this))"  maxlength="3" id="dias" />
					dias.
				</td>
			</tr>
						
			<tfoot align="center">
				<tr>
					<td colspan="5">
						<h:commandButton value="Buscar" action="#{expirarTempoCadastro.preEncerrar}" /> 
						<h:commandButton value="Cancelar" action="#{expirarTempoCadastro.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center><br/>


		<br/>
		
		<table class="listagem">
			<caption class="listagem">Lista das A��es de Extens�o Localizadas (${fn:length(expirarTempoCadastro.atividades)})</caption>
			<thead>
				<tr>
					<th>Ano</th>
					<th>T�tulo</th>
					<th>Tipo A��o</th>								
					<th>Data Cadastro</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{expirarTempoCadastro.atividades}" var="atv" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${atv.ano}</td>
						<td>${atv.titulo}</td>			
						<td>${atv.tipoAtividadeExtensao.descricao}</td>
						<td><fmt:formatDate value="${atv.dataCadastro}" pattern="dd/MM/yyyy HH:mm"/> </td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		
		<c:if test="${empty expirarTempoCadastro.atividades}">
			<center><font color='red'>Nenhuma a��o de extens�o foi encontrada.</font></center>
		</c:if>

		<h:panelGroup rendered="#{not empty expirarTempoCadastro.atividades}">
			<table class="formulario" width="100%" >
					<tfoot align="center">
						<tr>
							<td colspan="5">
								<h:commandButton value="Expirar Cadastros" action="#{expirarTempoCadastro.encerrar}"/>
								<h:commandButton value="Cancelar" action="#{expirarTempoCadastro.cancelar}" onclick="#{confirm}"/>
							</td>
						</tr>
					</tfoot>
			</table>
		</h:panelGroup>

  </h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
