	<h:form id="formTurma">
				<table class="formulario" width="80%">
					<caption>Informe os critérios de busca das turmas</caption>
					<tbody>
						<tr>
							<th  align="left" width="15%">Nível de Ensino:</th>
							<td colspan="2" align="left">
								<h:selectOneMenu id="inputNivel" value="#{consultaPublicaTurmas.nivelTurma}">
									<f:selectItem itemLabel=" -- SELECIONE -- " itemValue=""/>
									<f:selectItems value="#{nivelEnsino.allCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th   align="left" class="required">Unidade:</th>
							<td colspan="2" align="left">
								<h:selectOneMenu id="inputDepto" value="#{consultaPublicaTurmas.idDepto}">
									<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0"/>
									<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th  align="left" class="required">Ano - Período:</th>
							<td align="left" width="5%" nowrap="nowrap" colspan="2">
								<h:inputText  style="padding:1px;border-color:#888;"  value="#{consultaPublicaTurmas.anoTurma}"  size="4" maxlength="4" id="inputAno" onkeyup="return formatarInteiro(this,event);" /> -
 								<h:selectOneMenu  value="#{consultaPublicaTurmas.periodoTurma}" id="inputPeriodo">
									<f:selectItem itemLabel="1" itemValue="1"/>
									<f:selectItem itemLabel="2" itemValue="2"/>
									<f:selectItem itemLabel="3" itemValue="3"/>
									<f:selectItem itemLabel="4" itemValue="4"/>
								</h:selectOneMenu>
							</td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="3">
								<h:commandButton action="#{consultaPublicaTurmas.buscarTurmasGeral}" value="Buscar" style="padding: 1px;"/> &nbsp;
								<h:commandButton action="#{consultaPublicaTurmas.cancelar}" immediate="true" value="Cancelar" style="padding: 1px;"/>
							</td>
						</tr>	
					</tfoot>
						
					</table>
				
				<br clear="all"/>
					
			<c:if test="${not empty consultaPublicaTurmas.docenteTurma}">
				<div class="infoAltRem">
					<h:graphicImage url="/img/view.gif"/>
					:Visualizar Detalhes do Componente Curricular<br />
				</div>
					
				<div id="turmasAbertas">
					<table class="listagem" width="95%">
					<caption><h:outputText value="#{idioma.resultadoConsulta}"/> turmas encontrada(s)</caption>
						<thead>
							<tr>
								<th width="50px">Código</th>
								<th width="80px" align="center">Ano-Período</th>
								<th>Docente</th>
								<th width="50px;" align="center">Local</th>
							</tr>
						</thead>
						<tbody>
							<c:set var="disciplinaAtual" value="" />
							<c:forEach items="#{consultaPublicaTurmas.docenteTurma}" var="_dc" varStatus="status">
		
								<c:if test="${disciplinaAtual != _dc.turma.disciplina}">
									<tr  class="agrupador">
										<td colspan="4"	>
										<h:commandLink id="aqui" title="Visualizar Detalhes do Componente Curricular" action="#{componenteCurricular.detalharComponente}">								
											<h:graphicImage url="/img/view.gif"/>
											<f:param name="id" value="#{_dc.turma.disciplina.id}"/>
											<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>							
											<span class="tituloDisciplina">${_dc.turma.disciplina.descricaoResumida}</span>
										</h:commandLink>
										</td>
									</tr>
								</c:if>
								
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td class="turma" align="center"> ${_dc.turma.codigo}</td>
									<td class="anoPeriodo" align="center">${_dc.turma.ano}.${_dc.turma.periodo}</td>
									<td class="nome">${_dc.docente.pessoa.nome} (${_dc.chDedicadaPeriodo}h)</td>
									<td nowrap="nowrap" align="center"> ${_dc.turma.local}</td>
								</tr>
								
							  <c:set var="disciplinaAtual"  value="${_dc.turma.disciplina}"/>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
							<td colspan="4" align="center"> <b>${fn:length(consultaPublicaTurmas.docenteTurma)} turmas encontrada(s) </b></td>
							</tr>
						</tfoot>
				</table>
			</div>	
		</c:if>
		
	</h:form>		