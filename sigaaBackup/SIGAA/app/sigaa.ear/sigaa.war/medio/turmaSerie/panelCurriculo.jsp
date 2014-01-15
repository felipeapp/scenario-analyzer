<%-- Cadastro de Turma --%>
<rich:modalPanel id="panelCurriculo" autosized="true" minWidth="700">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="ESTRUTURA CURRICULAR"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
		<h:panelGroup>
			<h:graphicImage value="/img/close.png" styleClass="hidelink"
				id="hidelink" />
			<rich:componentControl for="panelCurriculo" attachTo="hidelink" operation="hide" event="onclick" />
		</h:panelGroup>
	</f:facet>
	
	<h:form id="formulario">

		<table class="formulario" width="700px">
			<caption class="formulario">Dados da Estrutura Curricular</caption>
			<tr>
				<th class="rotulo" width="50%">Código do Currículo: </th>
				<td align="left"><h:outputText value="#{turmaSerie.curriculoMedio.codigo }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Curso: </th>
				<td align="left"><h:outputText value="#{turmaSerie.curriculoMedio.cursoMedio.nome }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Série: </th>
				<td align="left"><h:outputText
					value="#{turmaSerie.curriculoMedio.serie.descricaoCompleta}" /></td>
			</tr>
			<tr>
				<th class="rotulo">Carga Horária Total:</th>
				<td align="left"><h:outputText value="#{turmaSerie.curriculoMedio.cargaHoraria}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano de Entrada em Vigor:</th>
				<td align="left"><h:outputText value="#{turmaSerie.curriculoMedio.anoEntradaVigor}" /></td>
			</tr>
			<tr>
				<th class="rotulo">Prazo de Conclusão: </th>
				<td align="left"><h:outputText value="#{turmaSerie.curriculoMedio.unidadeTempo.descricao}"/></td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="listagem" width="100%" >
					<caption class="listagem" style="text-align: center;">Disciplinas</caption>
					<c:set value="0" var="ch" />
					<c:if test="${not empty turmaSerie.curriculoMedio.curriculoComponentes}">
					<c:forEach var="linha" items="#{turmaSerie.curriculoMedio.curriculoComponentes}" varStatus="status">
						<c:set value="${ch + linha.chAno }" var="ch" />
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${linha.componente.nome} - ${linha.chAno}h</td>
						</tr>		
					</c:forEach>
					</c:if>
					<c:if test="${empty turmaSerie.curriculoMedio.curriculoComponentes}">
						<tr><td align="center">Nenhuma Disciplina Vinculada a Estrutura Curricular.</td></tr> 
					</c:if>
					
					<tr style="background-color: #EFEBDE">
						<td><b>CH Total da Disciplinas:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
					</tr>
					
				</table>
				</td>					
			</tr>	
		</table>
		<div style="width:100%;text-align:center;height:25px;background:#DFE8F6;vertical-align:middle;padding-top:5px;">
		<h:commandButton id="bFechar" value="Fechar">
	    	<rich:componentControl for="panelCurriculo" attachTo="bFechar" operation="hide" event="onclick" />
	    </h:commandButton>
	  
	</div>
	</h:form>
	
</rich:modalPanel>

