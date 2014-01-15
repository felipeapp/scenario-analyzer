<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/javascript/avaliacao_monitoria.js"></script>

<h2 class="title"><ufrn:subSistema /> > Avaliação de Projetos com Discrepância</h2>

<f:view>

<h:form>
	<h:messages showDetail="true"/>
	
	<table class="formulario" width="100%">
	<caption>Avaliar Projeto de Ensino</caption>	
		
		<tr>
			<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption><h:outputText value="#{avalProjetoMonitoria.obj.projetoEnsino.titulo}" id="projetoAvaliado"/></caption>
					<thead>
						<tr><td width="80%">AVALIAÇÕES DA COMISSÃO</td><td width="10%" align="center">Nota</td><td width="10%" align="center">Máximo</td></tr>
					</thead>
					<tr>
						<td width="35%"><b>AVALIADOR 1: </b></td>
						<td>
							<h:outputText value="#{ avalProjetoMonitoria.projeto.notaPrimeiraAvaliacao }">
								<f:convertNumber pattern="#0.00"/>
							</h:outputText>
						</td>
						<td><fmt:formatNumber pattern="#0.00" value="10.0"/></td>
					</tr>
					<tr>
						<td><b>AVALIADOR 2: </b></td>
						<td>
							<h:outputText value="#{ avalProjetoMonitoria.projeto.notaSegundaAvaliacao }">
								<f:convertNumber pattern="#0.00"/>
							</h:outputText>
						</td>
						<td><fmt:formatNumber pattern="#0.00" value="10.0"/></td>
					</tr>
					<tr>
						<td><b>MÉDIA: </b></td>
						<td>
							<h:outputText value="#{ avalProjetoMonitoria.projeto.mediaAnalise }">
								<f:convertNumber pattern="#0.00"/>
							</h:outputText>
						</td>
						<td><fmt:formatNumber pattern="#0.00" value="10.0"/></td>
					</tr>
				</table>
			</td>
		</tr>
		
		
		
		
		
		
		
	
	
	<c:forEach items="${ avalProjetoMonitoria.grupos }" var="grupo" varStatus="status">
		<tr>
			<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>${status.count} - ${ grupo.denominacao }</caption>
					<thead>
						<tr><td width="80%">Item</td><td width="10%" align="center">Nota</td><td width="10%" align="center">Máximo</td></tr>
					</thead>
					<c:forEach items="${ grupo.notas }" var="nota" varStatus="loop">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${status.count}.${loop.count} - ${ nota.itemAvaliacao.descricao }</td>
							<td><input style="text-align: right;" type="text" name="nota_${ grupo.id }_${ loop.index }" onkeydown="return(formataValor(this, event, 2))" maxlength="4" onkeyup="calculaTotal(${ grupo.id })" value="<fmt:formatNumber pattern="#0.00" value="${ nota.nota }"/>" class="nota_${ grupo.id } nota" size="5"/></td>
							<td><fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
						</tr>
					</c:forEach>
					<tr><td align="right"><strong>Total: </strong></td><td align="center"><strong><span id="total_${ grupo.id }"><fmt:formatNumber pattern="#0.00" value="${ grupo.totalGrupo }"/></span></strong></td><td></td></tr>
				</table>
				<br/>&nbsp;
			</td>
		</tr>
	</c:forEach>
	
	<tr bgcolor="#C8D5EC">
		<td colspan="2" align="center"><strong>Total: <span id="total"><fmt:formatNumber pattern="#0.00" value="${ avalProjetoMonitoria.notaTotal }"/></span></strong></td>
	</tr>
	
	<tr>
		<td>
		Parecer: <br/>
		<h:inputTextarea value="#{avalProjetoMonitoria.obj.parecer}" style="width: 98%" rows="4" id="parecer"/></td>
	</tr>
	<tr><td colspan="2"></td></tr>
	<tfoot>
		<tr>
			<td colspan="2">
			<h:commandButton value="Avaliar Projeto" action="#{avalProjetoMonitoria.avaliaProjetoDiscrepancia}" />
			<h:commandButton value="<< Voltar" action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" immediate="true" />
			<h:commandButton value="Cancelar" action="#{avalProjetoMonitoria.cancelar}" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>
	
	</table>
</h:form>



</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>