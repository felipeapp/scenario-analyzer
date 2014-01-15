<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>

<style>
	.descricaoOperacao{	font-size: 1.2em;}

	h3, h4 {font-variant: small-caps;text-align: center;margin: 2px 0 20px;}	
	
	h4 { margin: 15px 0 20px; }
	
	.descricaoOperacao p { text-align: justify; } 
	
	.codVer{text-align: center;display: block;position: relative;width: 100%;}
	
	.maiuscula{text-transform: capitalize;}	
	
	.sublinhado{border-bottom: 1px solid #000;} 
	
	.innerTable{border:0px !important;padding:0px !important;margin:0px !important;}
	
	table.dr-table,table.dr-table div, table.dr-table td, table.rich-table, .dr-pnl,.dr-table-cell,.radio,.checkbox {border:0px !important;padding: 0;margin:0px;font-family: inherit;}
	table.dr-table .pergunta{font-weight: bold;}
	.dr-pnl-b{padding-top: 1;padding-bottom: 1;}
</style>

<f:view>
	<h:form>
	<h2> Pró-Reitoria de Graduação, Departamento de Administração Escolar. </h2>
	<h2 class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.nome)}</h2>
	<h3>Formulário de Requerimento de Inscrição - Número ${inscricaoSelecao.obj.numeroInscricao} </h3>

	
	<table class="tabelaRelatorio" width="100%">
		<caption>Dados Pessoais</caption>
		<body>
			<tr>
				<th width="95px">CPF:</th>
				<td colspan="5"><ufrn:format type="cpf_cnpj" valor="${inscricaoSelecao.obj.pessoaInscricao.cpf}"/></td>
			</tr>	
			<tr>
				<td colspan="6" class="innerTable">
					<table width="100%">
						<th width="90px">Nome:</th>
						<td class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.nome)}</td>
						<th width="145px">Data de Nascimento:</th>
						<td ><fmt:formatDate value="${inscricaoSelecao.obj.pessoaInscricao.dataNascimento}"/></td>
					</table>
				</td>	
			</tr>
			
			<%-- Respostas do Questionário  --%>
			<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.questionario}">
				<tr>
					<td colspan="6" class="innerTable">
						<rich:dataTable  var="resposta" value="#{questionarioRespostasBean.respostasModel}" width="100%" id="dataTableQuestionario" rowKeyVar="row">
						
							<rich:column>
								<h:outputText value=" #{resposta.pergunta.pergunta}:" styleClass="pergunta" /> 
								
								<h:panelGroup rendered="#{not resposta.pergunta.multiplaOuUnicaEscolha}">
									<rich:panel>
										<h:outputText value="#{resposta.respostaVf ? 'Verdadeiro' : 'Falso'}" rendered="#{resposta.pergunta.vf}"/>
										
										<h:outputText value="#{resposta.respostaDissertativa}" rendered="#{resposta.pergunta.dissertativa}"/>
										
										<h:outputText value="#{resposta.respostaNumerica}" rendered="#{resposta.pergunta.numerica}"/>
									</rich:panel>
								</h:panelGroup>
							</rich:column>
							
							<rich:subTable var="alternativa" value="#{resposta.pergunta.alternativas}" rendered="#{resposta.pergunta.unicaEscolha || resposta.pergunta.unicaEscolhaAlternativaPeso}" rowClasses="alternativa" >
								<rich:column styleClass="alternativa">
									<span class="radio <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
										<span class="esconder"> 
								       		<b><h:outputText value="#{alternativa.gabarito ? '(X)' : '( )'}"/></b>
								    	</span>			 
									 
										<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
									</span>
								</rich:column>
						    </rich:subTable>
						
							<rich:subTable var="alternativa" value="#{resposta.pergunta.alternativas}" rendered="#{resposta.pergunta.multiplaEscolha}"  rowClasses="alternativa">
								<rich:column styleClass="alternativa">
									<span class="checkbox <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
										<span class="esconder"> 
								       		<b><h:outputText value="#{alternativa.gabarito ? '[X]' : '[ ]'}"/></b>
								    	</span>			
										<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
									</span>
								</rich:column>
							</rich:subTable>
						
						</rich:dataTable>
					</td> 
				</tr>
			</c:if>
			
			<tr>
				<td colspan="6" class="innerTable">
					<table width="100%">
						<th width="90px">Identidade:</th>
						<td>${inscricaoSelecao.obj.pessoaInscricao.identidade.numero}</td>
						<th width="140px">Orgão Expedidor/UF:</th>
						<td>${fn:toUpperCase(inscricaoSelecao.obj.pessoaInscricao.identidade.orgaoExpedicao)}/${fn:toUpperCase(inscricaoSelecao.obj.pessoaInscricao.identidade.unidadeFederativa.sigla)}</td>
						<th width="130px">Data de Expedição:</th>
						<td><fmt:formatDate  value="${inscricaoSelecao.obj.pessoaInscricao.identidade.dataExpedicao}"/></td>
					</table>
				</td>
			</tr>
				
			<tr>	
			</tr>
			
			<tr>
				<td colspan="6" class="innerTable">
					<table width="100%">
						<th  width="90px">Endereço:</th>
						<td class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.logradouro)}</td>
						<th width="30px">Nº:</th>
						<td>${inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.numero}</td>
					</table>
				</td>		
			</tr>
				
			<tr>
				<td colspan="6" class="innerTable">
					<table width="100%">			
						<th width="90px">Bairro:</th>
						<td class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.bairro)}</td>
						<th width="90px">Município:</th>
						<td class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.municipio.nome)}</td>
						<th width="30px">UF:</th>
						<td>${inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.unidadeFederativa.sigla}</td>
					</table>
				</td>		
			</tr>
			
			<tr>
				<th width="95px">Complemento:</th>
				<td colspan="5" class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.enderecoResidencial.complemento)}</td>
			</tr>
			
			</tbody>
		</table>	
		
		
		<table class="tabelaRelatorio" width="100%">
		<caption>Dados do Processo Seletivo</caption>
		<body>
			
				<c:choose>
					<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.curso}">
					<tr>	
						<th>Curso:</th>
						<td class="maiuscula">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.nomeCompleto)}
						</td>
						<th width="60px">Nível de Ensino:</th>
						<td class="maiuscula"  colspan="3">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.nivelDescricao)}
						</td>
					</tr>	
					</c:when>
					<c:otherwise>
					<tr>
						<th width="120px">Curso/Habilitação:</th>
						<td class="maiuscula">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.descricao)}<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular.habilitacao.nome}">
							/ ${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.habilitacao.nome)}	
							</c:if>
						</td>
						<th>Modalidade:</th>
						<td class="maiuscula"  colspan="3">
							<c:choose>
								<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.descricao}">
									${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.descricao)}
								</c:when>
								<c:otherwise>
									${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.titulo)}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>		
						<th>Turno:</th>
						<td>
							${inscricaoSelecao.obj.processoSeletivo.matrizCurricular.turno.descricao}
						</td>
						<th width="60px">Cidade:</th>
						<td class="maiuscula" colspan="3">
							${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.municipio.nome)}
						</td>
					</tr>
					</c:otherwise>
				</c:choose>	
					
					<tr>	
					<c:choose>	
						<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular && inscricaoSelecao.obj.processoSeletivo.possuiAgendamento}">
							<th>Data Agendada:</th>
							<td colspan="3"><ufrn:format type="data" valor="${inscricaoSelecao.obj.agenda.dataAgenda}" /></td>
						</c:when>
						<c:otherwise>
							<td></td>
							<td colspan="3"></td>
						</c:otherwise>
					</c:choose>
					</tr>
			</tbody>
		</table>

		<br/>
		<br/>
		<br/>
		
		<table width="100%">
			<tr>			
				<th width="80px"><b>Assinatura:</b></th>
				<td class="sublinhado" width=""></td>
				<th width="50px"><b>Data:</b></th>
				<td class="sublinhado" width="100px"></td>
			</tr>	
		</table>
	
		<br/>
		<br/>		

		<%-- USO EXCLUSIVO DA GRADUAÇÃO - TRANSFERÊNCIA VOLUNTÁRIA --%>
		<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular}">
			<table class="tabelaRelatorio" width="100%">
				<caption>Documentação (Para uso exclusivo do ${ configSistema['siglaUnidadeGestoraGraduacao'] })</caption>
				<tr>
					<td>
					<%@include file="/public/processo_seletivo/documentacao.jsp" %>
					</td>
				</tr>
			</table>
		</c:if>
			
		<div class="descricaoOperacao">
			<center>Código Verificador: ${inscricaoSelecao.obj.codigoHash}</center>
		</div>
		
	</h:form>
</f:view>
<script>//print();</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>