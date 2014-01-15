<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Confirma��o da Avalia��o do Projeto</h2>
	<h:form id="formConfirmaAvaliacao">
	
	<table class="formulario" width="95%">
		<caption>Confirma��o da Avalia��o do Projeto</caption>
		<tr>
			<td>
				<table width="100%" class="formulario">
					<tr>
						<th class="rotulo" width="15%">T�tulo Projeto:</th>
						<td>${avaliacaoProjetoBean.obj.projeto.anoTitulo}</td>							
					</tr>
	
					<tr>
						<th class="rotulo">Avaliador:</th>
						<td>${avaliacaoProjetoBean.obj.avaliador.pessoa.nome}</td>							
					</tr>
					
					<tr>
						<th class="rotulo">Data Avalia��o:</th>
						<td><fmt:formatDate value="${avaliacaoProjetoBean.obj.dataAvaliacao}" pattern="dd/MM/yyyy HH:mm:ss" /></td>							
					</tr>					
					
				</table>
		  </td>
		</tr>
		<tr>
			<td>
				<table width="100%" class="subFormulario">
					<caption>${avaliacaoProjetoBean.obj.distribuicao.modeloAvaliacao.questionario.descricao}<caption>
					
					<thead>
						<tr>
							<td>Descri��o do Item Avaliado</td>
							<td width="7%" style="text-align: right;">Nota</td>
							<td width="7%" style="text-align: right;">M�ximo</td>
							<td width="7%" style="text-align: right;">Peso</td>
						</tr>	
					</thead>
				
					<c:forEach items="${ avaliacaoProjetoBean.obj.notas }" var="nota" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${ nota.itemAvaliacao.pergunta.descricao }</td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.nota }"/></td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.itemAvaliacao.notaMaxima }"/></td>
							<td style="text-align: right;"><fmt:formatNumber pattern="#0.0" value="${ nota.itemAvaliacao.peso }"/></td>
						</tr>
					</c:forEach>

				</table>
				<br/>&nbsp;
			</td>
		</tr>
		
		<tr bgcolor="#C8D5EC">
			<td colspan="2" align="center"><strong>Total Avalia��o: <span id="total"><fmt:formatNumber pattern="#0.0" value="${ avaliacaoProjetoBean.obj.nota }"/></span></strong></td>
		</tr>
			
		<tr>
			<td>
				<b>Parecer: </b><br/>
				<h:outputText style="text-align: justify;" id="outParecer" value="#{avaliacaoProjetoBean.obj.parecer}" >
					<f:attribute name="lineWrap" value="95" />
					<f:converter converterId="convertTexto"/>
				</h:outputText>
				
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td>					
					<h:commandButton id="btConfirmarAvaliacao" value="Confirmar Avalia��o"action="#{avaliacaoProjetoBean.confirmarAvaliacao}"/>
					<h:commandButton id="btVoltar" value="<< Voltar"action="#{avaliacaoProjetoBean.redirecionaPaginaAvaliacao}"/>
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoBean.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>