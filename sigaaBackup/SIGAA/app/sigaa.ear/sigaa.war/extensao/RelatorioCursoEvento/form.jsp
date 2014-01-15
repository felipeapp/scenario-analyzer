<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;  	
	width: 98%;
	overflow:auto;
}
</style>


<script type="text/javascript">
	function acaoRealizada(acaoRealizada) {
		var motivo = document.getElementById("motivoNaoRealizada") ;
		
		if (acaoRealizada.value == 'TRUE') {
			motivo.style.display = "none";
		} else {
			motivo.style.display = "";
		}		
	}
</script>


<f:view>

	<h:outputText value="#{relatorioCursoEvento.create}"/>
	<h:outputText value="#{participanteAcaoExtensao.create}"/>

	<h2><ufrn:subSistema /> > Relatório de Cursos & Eventos de Extensão</h2>

	<h:form id="form" enctype="multipart/form-data">
		
		<table class="formulario" width="100%">
		<caption class="listagem">CADASTRO DE ${relatorioCursoEvento.obj.tipoRelatorio.descricao} DE CURSOS & EVENTOS DE EXTENSÃO</caption>
			
		<tr>
				<th width="25%"><b>Código:</b></th>
				<td><h:outputText	value="#{relatorioCursoEvento.obj.atividade.codigo}" /></td>
		</tr>

		<tr>
				<th><b>Título:</b></th>
				<td><h:outputText	value="#{relatorioCursoEvento.obj.atividade.titulo}"/></td>
		</tr>

		<tr>
				<th><b>Unidade proponente:</b></th>
				<td><h:outputText value="#{relatorioCursoEvento.obj.atividade.unidade.nome}" /></td>
		</tr>
		
		<tr>
				<th><b>Fontes de financiamento:</b></th>
				<td><h:outputText value="#{relatorioCursoEvento.obj.atividade.fonteFinanciamentoString}" /></td>
		</tr>

		
		<tr>
				<th><b>Nº de Discentes envolvidos:</b></th>
				<td>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.totalDiscentes}"/>
					<ufrn:help img="/img/ajuda.gif">Total de discentes envolvidos na execução do Curso ou Evento.</ufrn:help>					
				</td>
		</tr>
		
		<tr>
			<th class="required"><b>Esta Ação foi realizada:</b></th>
			<td><h:selectOneRadio value="#{ relatorioCursoEvento.obj.acaoRealizada }" onclick="javascript:acaoRealizada(this)" id="acaoFoiRealizada">
					<f:selectItem itemLabel="SIM" itemValue="TRUE" />
					<f:selectItem itemLabel="NÃO" itemValue="FALSE" />
				</h:selectOneRadio>
			</td>
		</tr>


		<tr>
			<td  colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
		</tr>	

		<tr>
			<td colspan="2">
				<div id="motivoNaoRealizada" style="display: ${relatorioCursoEvento.obj.acaoRealizada == null ? 'none' : (relatorioCursoEvento.obj.acaoRealizada ? 'none' : '')};">
					<b> Motivo da não realização desta ação: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
					<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.motivoAcaoNaoRealizada}" readonly="#{relatorioCursoEvento.readOnly}" id="motivoAcaoNaoRealizada"/>
				</div>
			</td>	
		</tr>

		<tr>
			<td colspan="2"><b> Atividades realizadas: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.atividadesRealizadas}" readonly="#{relatorioCursoEvento.readOnly}" id="atividades"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Resultados obtidos: qualitativos. </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.resultadosQualitativos}" readonly="#{relatorioCursoEvento.readOnly}" id="qualitativos"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Resultados obtidos: quantitativos. </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.resultadosQuantitativos}" readonly="#{relatorioCursoEvento.readOnly}" id="quantitativos"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Dificuldades encontradas: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.dificuldadesEncontradas}" readonly="#{relatorioCursoEvento.readOnly}" id="dificuldades"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Ajustes realizados durante a execução da ação de extensão: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioCursoEvento.obj.ajustesDuranteExecucao}" readonly="#{relatorioCursoEvento.readOnly}" id="ajustes"/>
			</td>	
		</tr>


		<tr>
			<th><b> Público estimado:</b></th>
			<td>
				<h:outputText value="#{relatorioCursoEvento.obj.atividade.publicoEstimado}" id="publicoEstimado"/> pessoas
				<ufrn:help img="/img/ajuda.gif">Público estimado informado durante o cadastro da proposta do curso ou evento.</ufrn:help>
			</td>	
		</tr>

		
		<tr>
			<th class="required"><b> Público real atingido:</b></th>
			<td>			
				<h:inputText style="text-align: right" title="Público real atingido" value="#{relatorioCursoEvento.obj.publicoRealAtingido}" readonly="#{relatorioCursoEvento.readOnly}" id="publicoRealAtingido" size="10" onkeyup="return formatarInteiro(this)" maxlength="9"/> pessoas
			</td>	
		</tr>

		<tr>
			<th><b> Total de concluintes:</b></th>
			<td>			
				<h:inputText style="text-align: right" title="Total de concluintes" value="#{relatorioCursoEvento.obj.numeroConcluintes}" readonly="#{relatorioCursoEvento.readOnly}" id="concluintes" size="10" onkeyup="return formatarInteiro(this)" maxlength="9"/> pessoas
			</td>	
		</tr>

		<tr>
			<th><b>Valor da Taxa de matrícula:</b></th>
			<td>
				R$ <h:inputText value="#{relatorioCursoEvento.obj.taxaMatricula}"	id="taxaMatricula" size="12" 
					onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioCursoEvento.readOnly}" style="text-align: right">
						<f:convertNumber pattern="#,##0.00"/>
				</h:inputText>
			</td>
		</tr>
		
		<tr>
			<th><b>Valor arrecadado:</b></th>
			<td>
				R$ <h:inputText value="#{relatorioCursoEvento.obj.totalArrecadado}"  id="totalArrecadado" size="12"
					onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioCursoEvento.readOnly}" style="text-align: right" maxlength="12">
						<f:converter converterId="convertMoeda"/>
				</h:inputText>
			</td>
		</tr>
		

		<tr>
			<td colspan="2" class="subFormulario"> Detalhamento de utilização dos recursos financeiros </td>
		</tr>	

		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
									<tr>
										<td>
													<c:if test="${not empty relatorioCursoEvento.obj.detalhamentoRecursos}">
																	<t:dataTable id="dt" value="#{relatorioCursoEvento.obj.detalhamentoRecursos}" var="consolidacao"
																	 width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
																				<t:column>
																					<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
																					<h:outputText value="#{consolidacao.elemento.descricao}" />
																				</t:column>

																				<t:column>
																					<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:inputText value="#{consolidacao.faex}"  id="fundo" size="12" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioCursoEvento.readOnly}" style="text-align: right">
																							<f:converter converterId="convertMoeda"/>
																					</h:inputText>
																				</t:column>

																				<t:column>
																					<f:facet name="header"><f:verbatim>Funpec</f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:inputText value="#{consolidacao.funpec}" id="fundacao"  size="12"  onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioCursoEvento.readOnly}" style="text-align: right">
																							<f:converter converterId="convertMoeda"/>
																					</h:inputText>
																				</t:column>


																				<t:column>
																					<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:inputText value="#{consolidacao.outros}" id="outros" size="12" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioCursoEvento.readOnly}" style="text-align: right" >
																							<f:converter converterId="convertMoeda"/>
																					</h:inputText>
																				</t:column>
																</t:dataTable>
													</c:if>
										</td>
								</tr>
							</thead>
							
						<c:if test="${empty relatorioCursoEvento.obj.detalhamentoRecursos}">
							<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
						</c:if>
							
					</table>
			</td>
		</tr>
		

		

		<tr>
			<td colspan="2" class="subFormulario"> Anexar Arquivo com outros detalhes da execução da ação </td>
		</tr>	


		<tr>
			<th  class="required"> Descrição:</th>
			<td>
				<h:inputText  id="descricao" value="#{relatorioCursoEvento.descricaoArquivo}" size="60" maxlength="90" readonly="#{relatorioCursoEvento.readOnly}" />
			</td>
		</tr>
		
		<tr>
			<th width="20%">Arquivo:</th>
			<td>
				<t:inputFileUpload id="uFile" value="#{relatorioCursoEvento.file}" storage="file" disabled="#{relatorioCursoEvento.readOnly}" size="50"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">
				<center><h:commandButton action="#{relatorioCursoEvento.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui" disabled="#{relatorioCursoEvento.readOnly}"/></center>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<div class="infoAltRem">
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" styleClass="noborder"/>: Remover Arquivo
				    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder"/>: Ver Arquivo				    
				</div>
			</td>
		</tr>	

		<tr>
			<td colspan="2">
				<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
				<input type="hidden" value="0" id="idArquivoRelatorio" name="idArquivoRelatorio"/>
	
				<t:dataTable id="dataTableArq" value="#{relatorioCursoEvento.arquivosRelatorio}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column  width="97%">
						<f:facet name="header"><f:verbatim>Descrição do Arquivo</f:verbatim></f:facet>
						<h:outputText value="#{anexo.descricao}" />
					</t:column>
	
					<t:column>
						<h:commandButton image="/img/delete.gif" action="#{relatorioCursoEvento.removeAnexo}"
							title="Remover Arquivo"  alt="Remover Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo};$(idArquivoRelatorio).value=#{anexo.id};return confirm('Deseja Remover este Arquivo do Relatório?')" id="remArquivo" />
					</t:column>
					<t:column>
						<h:commandButton image="/img/view.gif" action="#{relatorioCursoEvento.viewArquivo}"
							title="Ver Arquivo"  alt="Ver Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo};" id="viewArquivo" />
					</t:column>	
				</t:dataTable>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Lista de Participantes do Curso/Evento </td>
		</tr>	
		
		<tr>
			<td colspan="2">
					<table class="listagem">
							<thead>
							<tr>
								<th style="text-align: right;">Nº</th>
								<th style="text-align: center;">CPF</th>
								<th>Nome</th>
								<th>Participação</th>
								<th style="text-align: right;">Frequência</th>								
								<th style="text-align: center;">Certificado</th>
								<th></th>
							</tr>
							</thead>
				
							<tbody>
								<c:forEach items="#{relatorioCursoEvento.obj.atividade.participantes}" var="participante" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td style="text-align: right;">${status.count}</td>
											<td style="text-align: center;">${participante.cadastroParticipante.cpf}</td>
											<td>${participante.cadastroParticipante.nome}</td>
											<td>${participante.tipoParticipacao.descricao}</td>			
											<td style="text-align: right;">${participante.frequencia}%</td>
											<td style="text-align: center;">${participante.autorizacaoCertificado ? 'SIM' : 'NÃO'}</td>
										</tr>
								</c:forEach>
								
								<c:if test="${empty relatorioCursoEvento.obj.atividade.participantes}">
									<tr><td colspan="5"><center><i>Não há participantes cadastrados</i></center></td></tr>
								</c:if>				
							</tbody>
					</table>
			
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="2">
						<c:if test="${relatorioCursoEvento.relatorioFinal && relatorioCursoEvento.existeRelatorioParcial}">
						<h:commandButton immediate="true" value="Importar Dados do Relatório Parcial" action="#{relatorioCursoEvento.importarDadosRelatorioParcial}" 
							rendered="#{!relatorioCursoEvento.readOnly }" />
					</c:if>
						<h:commandButton value="Salvar (Rascunho)" action="#{relatorioCursoEvento.salvar}" rendered="#{!relatorioCursoEvento.readOnly}"/>	
						<h:commandButton value="#{relatorioCursoEvento.confirmButton}" action="#{relatorioCursoEvento.enviar}" rendered="#{!relatorioCursoEvento.readOnly}"/> 
						<h:commandButton value="#{relatorioCursoEvento.confirmButton}" action="#{relatorioCursoEvento.removerRelatorio}" rendered="#{relatorioCursoEvento.readOnly}"/>
						<h:commandButton value="Cancelar" action="#{relatorioCursoEvento.cancelar}" onclick="#{confirm }" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br />
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>