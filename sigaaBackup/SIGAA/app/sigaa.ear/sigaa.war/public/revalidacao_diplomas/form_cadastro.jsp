<%@include file="/public/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>

<style>
#cepIndicator {padding: 0 25px;color: #999;}
.descricaoOperacao {font-size: 1.2em;}
.descricaoOperacao p {text-align: justify;}
h3,h4 {font-variant: small-caps;text-align: center;margin: 2px 0 20px;}
h4 {margin: 15px 0 20px;}
.vermelho{color: #FF1111;}
</style>

<f:view>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />

	<c:set var="todasDatas" value="#{solRevalidacaoDiploma.allComboData}" />
	<h2>Ficha de Inscrição para Revalidação de Diploma</h2>
	<div class="descricaoOperacao">
		<center>
		<b><h:outputText value="#{solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.titulo}"/></b>
		</center>
		<p>
			<ul>
				<li>
					O período para inscrição na WEB será de
					<b><ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.inicioInscricao}"/></b> à
					<b><ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.finalInscricao}"/></b>.
				</li> 
				<c:if test="${not empty todasDatas}">
				<li>
					Preencha o formulário de inscrição para agendar a entrega dos documentos, que está compreendida entre 
					<b><ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.inicioAgenda}"/></b> e 
					<b><ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.finalAgenda}"/></b>. 
				</li> 
				</c:if>
				<li>	
					Caso os campos data e horário de agendamento não estejam disponíveis, 
					o inscrito aguardará um novo período para seu reagendamento. 
				</li>
			</ul>	
		</p>
		<p>
			<b>Atenção candidato</b>, para confirmar sua inscrição no processo de revalidação de diploma, informe todos os dados corretamente. 
			A sua inscrição só será finalizada com a exibição do <b>comprovante</b>, que deverá ser <b>impresso</b> juntamente com a <b>ficha 
			de inscrição</b> para sua maior segurança.
		</p>
		<c:if test="${empty todasDatas}">
		<p  class="vermelho">
			Informamos que seu cadastro será adicionado a uma lista de espera e no período do dia ${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.periodoReagendamento}
			 será disponibilizado novas datas e horários para o reagendamento. Nesta situação o inscrito deverá acessar novamente o SIGAA, selecionar a nacionalidade, digitar o seu CPF, clicar em "visualizar".
		</p>
		</c:if>
		<h4>
			<a	href="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.urlEdital}">
			Clique Aqui Para maiores informações veja o edital 
			</a>
		</h4>
	</div>
	

	<h:form id="form">

		<table class="subFormulario" width="98%">
			<!-- ============= DADOS PESSOAIS ============ -->
			<caption>Dados Pessoais</caption>
			<tbody>


				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" border="0">
							
							<tr>
								<th class="required">Nome:</th>
								<td>
									<h:inputText style="visualizarFicha" value="#{solRevalidacaoDiploma.obj.nome}" id="nome" size="88"
									maxlength="60"  readonly="#{solRevalidacaoDiploma.readOnly}" />
								</td>
							</tr>
			
							<tr>
								<th class="required">Nome da Mãe:</th>
								<td><h:inputText
									value="#{solRevalidacaoDiploma.obj.nomeMae}" id="mae" size="88"
									maxlength="60" readonly="#{solRevalidacaoDiploma.readOnly}" /></td>
							</tr>
							<tr>
								<th width="150px;">Nome do Pai:</th>
								<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.nomePai}" id="pai" size="88"
									maxlength="60" readonly="#{solRevalidacaoDiploma.readOnly}" />
								</td>
							</tr>
						</table>
					</td>
				</tr>			
				<!-- ============= NACIONALIDADE ============ -->
				
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" border="0">
							<tr>
								<th class="required" width="150px">Nacionalidade:</th>
								<td  width="120px">
									<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.pais.id}" id="nacionalidade"
										readonly="#{solRevalidacaoDiploma.readOnly}">
											<f:selectItems value="#{pais.allCombo}" />
											<a4j:support event="onchange" reRender="tableDocumento"/>
									</h:selectOneMenu>
								</td>
					
								<th class="required"  width="125px">Naturalidade:</th>
								<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.natural}" maxlength="60" size="40" id="natural"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>		
				
				<!-- ============= ENDEREÇO DE CONTATO ============ -->
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" border="0">
				
							<tr class="linhaCep">
								<th class="required" width="150px" >CEP:</th>
								<td colspan="3">
									<h:inputText value="#{solRevalidacaoDiploma.obj.cep}" maxlength="10"
									size="10" readonly="#{solRevalidacaoDiploma.readOnly}"
									id="endCEP"
									onkeyup="return formatarInteiro(this);"
									onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
									<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
									<img src="/sigaa/img/buscar.gif" />
									</a>
									<span class="info">(clique	na lupa para buscar o endereço do CEP informado)</span>
									<span	id="cepIndicator" style="display: none;"> 
									<img src="/sigaa/img/indicator.gif" /> Buscando endereço... </span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" border="0">
						<tr>
							<th class="required" width="150px">UF:</th>
							<td width="150px">
								<h:selectOneMenu style="width:200px"
									value="#{solRevalidacaoDiploma.obj.unidadeFederativa.id}"
									id="ufEnd" disabled="#{pessoaInscricao.readOnly}"  valueChangeListener="#{solRevalidacaoDiploma.carregarMunicipios}"
									onchange="submit()" immediate="true">
									<f:selectItems value="#{unidadeFederativa.allCombo}" />
								</h:selectOneMenu>
							</td>
							<th class="required">Município:</th>
							<td>
								<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.municipio.id}" id="endMunicipio"  
									readonly="#{solRevalidacaoDiploma.readOnly}">
									<f:selectItems value="#{solRevalidacaoDiploma.municipiosUf}" />
								</h:selectOneMenu>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" border="0">
						<tr>
							<td style="padding:0px;" align="left">
							<table width="100%" border="0">
							<tr>
								<th class="required"  width="150px">Logradouro:</th>
								<td width="490px">
									<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.tipoLogradouro.id}"
										readonly="#{solRevalidacaoDiploma.readOnly}" id="tipoLogradouro">
										<f:selectItems value="#{tipoLogradouro.allCombo}" />
									</h:selectOneMenu>
									&nbsp;
									<h:inputText value="#{solRevalidacaoDiploma.obj.logradouro }"
										disabled="#{solRevalidacaoDiploma.readOnly}" maxlength="100"
										id="logradouro" size="50" />
								</td>
								<th  class="required" width="40px">Nº:</th>
								<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.numero}" maxlength="8" size="6"
									readonly="#{solRevalidacaoDiploma.readOnly}" id="endNumero" onkeyup="return formatarInteiro(this);" />
								</td>
							</tr>
							</table>
							</td>
						</tr>		

						<tr>
							<td style="padding:0px;" align="left">
								<table width="100%" border="0">
									<tr>
									
									<th class="required" width="147px">Bairro:</th>
									<td width="150px">
										<h:inputText value="#{solRevalidacaoDiploma.obj.bairro}"	
										maxlength="60" size="30" readonly="#{solRevalidacaoDiploma.readOnly}" id="endBairro" />
									</td>
									<th class="required" width="80px">Telefone:</th>
									<td width="130px">
										(<h:inputText disabled="#{solRevalidacaoDiploma.readOnly}"
										value="#{solRevalidacaoDiploma.obj.telefone_ddd}" maxlength="2"
										size="2" id="dddFixoNumero" style="text-align:center" onkeyup="return formatarInteiro(this);" />) 
										<h:inputText disabled="#{solRevalidacaoDiploma.readOnly}"
										value="#{solRevalidacaoDiploma.obj.telefone}"
										onkeydown="formataTelefone(this,event)" maxlength="9" size="9"
										id="telFixoNumero" />
									</td>
									<th width="35px">Fax:</th>
									<td>
											(<h:inputText disabled="#{solRevalidacaoDiploma.readOnly}"
											value="#{solRevalidacaoDiploma.obj.fax_ddd}" maxlength="2"
											size="2" id="dddFax" style="text-align:center" onkeyup="return formatarInteiro(this);" />) 
											<h:inputText readonly="#{solRevalidacaoDiploma.readOnly}"
											value="#{solRevalidacaoDiploma.obj.fax}" onkeydown="formataTelefone(this,event)" maxlength="9" size="9"
											id="faxNumero" />
									</td>
									</tr>
								</table>
							</td>
						</tr>	
				
						<tr>
							<td style="padding:0px;" align="left">
								<table width="100%" border="0">
								<tr>
									<th class="required" width="147px">E-mail:</th>
									<td>
										<h:inputText value="#{solRevalidacaoDiploma.obj.email}" id="confirmaEmail" size="30"
										maxlength="80" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
									<th class="required" width="130px">Confirmar E-mail:</th>
									<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.confirmaEmail}" id="email" size="30"
										maxlength="80" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
		
								</tr>
								</table>
							</td>
						</tr>
							

					</table>
					</td>
				</tr>
				<tr>
					<th></th>
					<td></td>
				</tr>
				<!-- ============= DOCUMENTAÇÃO ============ -->
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
					
						<table width="100%" border="0" class="subFormulario">
							<caption>Documentação</caption>
	
								<t:htmlTag value="tbody"  id="tableDocumento">
								<tr>
									<th  class="${solRevalidacaoDiploma.nacionalidadeCadastro?'required':''}"  width="150px">RG:</th>
									<td width="100px"> <h:inputText
										value="#{solRevalidacaoDiploma.obj.rgNumero}" id="rg" maxlength="14"
										readonly="#{solRevalidacaoDiploma.readOnly}" size="12" /></td>
									<th width="150" class="${solRevalidacaoDiploma.nacionalidadeCadastro?'required':''}">Órgão de Expedição:</th>
									<td width="80px">
										<h:inputText value="#{solRevalidacaoDiploma.obj.rgOrgaoExpedicao}" size="10"
										readonly="#{solRevalidacaoDiploma.readOnly}" id="orgaoExpedicao"
										maxlength="20" />
									</td>
									<th width="140px" class="${solRevalidacaoDiploma.nacionalidadeCadastro?'required':''}">Data de Expedição:</th>
									<td>
										<t:inputCalendar value="#{solRevalidacaoDiploma.obj.rgDataExpedicao}" size="10"
										readonly="#{solRevalidacaoDiploma.readOnly}" id="dataExpedicao"
										maxlength="10" renderAsPopup="true"	renderPopupButtonAsImage="true"
										onkeydown="formataData(this,event)" popupDateFormat="dd/MM/yyyy" />
									</td>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${solRevalidacaoDiploma.nacionalidadeCadastro}">
											<th align="right" class="required" width="150">CPF:</th>
											<td align="left" colspan="4">
												<h:inputText value="#{solRevalidacaoDiploma.obj.cpf}"
												 size="14" maxlength="14" id="cpf"
												onkeypress="formataCPF(this, event, null)"  readonly="#{solRevalidacaoDiploma.readOnly}" >
												   <f:converter converterId="convertCpf"/>
         										   <f:param name="type" value="cpf" />
												</h:inputText>
											</td>
										</c:when>
										<c:otherwise>
												<th align="right" class="required" >Nº Passaporte:</th>
												<td align="left"  colspan="4">
												<h:inputText
												value="#{solRevalidacaoDiploma.obj.passaporte}" size="15"
												maxlength="25" id="passaporte" readonly="#{solRevalidacaoDiploma.readOnly}" /></td>
										</c:otherwise>
									</c:choose>
								</tr>
								</t:htmlTag>
							</table>
							
								
						
					</td>
				</tr>
				<tr>
					<th ></th>
					<td>
						
					</td>
				</tr>
				
					<!-- ============= DADOS DA INSTITUIÇÃO ============ -->
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
						<table width="100%" class="subFormulario">
						<caption>Dados da Instituição Emissora do Diploma</caption>
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
								<table width="100%" border="0">
									<tr>
									<th class="required" width="150">Nome:</th>
									<td colspan="3">
										<h:inputText value="#{solRevalidacaoDiploma.obj.universidade}"
										id="universidade" size="86" maxlength="80" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
									</tr>
								</table>
							</td>
						</tr>	
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
								<table width="100%" border="0">
								<tr>
									<td style="padding:0px;" align="left">					
									<th class="required"  width="150">Curso:</th>
									<td width="220px">
										<h:inputText value="#{solRevalidacaoDiploma.obj.curso}"
										id="curso" size="45" maxlength="200" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
									<th class="required" width="140px">Ano de Conclusão:</th>
									<td>
										<h:inputText value="#{solRevalidacaoDiploma.obj.anoConclusao}"  onkeyup="return formatarInteiro(this);" 
										id="anoConclusao" size="4" maxlength="4" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
								</tr>
								</table>
							</td>
						</tr>	
						
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
							<table width="100%" border="0">
								<tr>
									<th class="required" width="150px">Endereço:</th>
									<td width="300px">
										<h:inputText value="#{solRevalidacaoDiploma.obj.logradouroUniversidade}"
										id="logradouroUniversidade" maxlength="200" size="60" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
									<th class="required" width="45px">Nº:</th>
									<td>
										<h:inputText value="#{solRevalidacaoDiploma.obj.numeroUniversidade}"  onkeyup="return formatarInteiro(this);" 
										id="numeroUniversidade" size="5" maxlength="10" readonly="#{solRevalidacaoDiploma.readOnly}" />
									</td>
								</tr>
							</table>
							</td>
						</tr>		
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
							<table width="100%">
							<tr>
								<th class="required" width="150">País:</th>
								<td width="120px">
									<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.paisUniversidade.id}" id="paisUniversidade"
										disabled="#{solRevalidacaoDiploma.readOnly}">
										<f:selectItems value="#{pais.allCombo}" />
									</h:selectOneMenu>
								</td>
								<th class="required" width="150px">Cidade:</th>
								<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.cidadeUniversidade}"
									id="cidadeUniversidade" size="30" maxlength="100" readonly="#{solRevalidacaoDiploma.readOnly}" />
								</td>
							</tr>
							</table>
							</td>
						</tr>	
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
							<table width="100%">
							<tr>
							<th class="required" width="150">Página Eletrônica:</th>
							<td  colspan="3">
								<h:inputText value="#{solRevalidacaoDiploma.obj.paginaUniversidade}"
								id="paginaUniversidade" size="86" maxlength="200" readonly="#{solRevalidacaoDiploma.readOnly}" />
							</td>
							</tr>
							</table>
							</td>
						</tr>	
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
							<table width="100%">
							<tr>
								<th class="required"  width="150px">E-mail da Instituição:</th>
								<td   width="120px">
									<h:inputText value="#{solRevalidacaoDiploma.obj.emailUniversidade}"
									id="emailUniversidade" size="30" maxlength="80" readonly="#{solRevalidacaoDiploma.readOnly}" />
								</td>
								<th class="required" width="122">Confirmar E-mail:</th>
								<td>
									<h:inputText value="#{solRevalidacaoDiploma.obj.confirmaEmailUniversidade}"
									id="confirmaEmailUniversidade" size="30"  maxlength="80" readonly="#{solRevalidacaoDiploma.readOnly}" />
								</td>
							</tr>	
							</table>
							</td>
						</tr>		
							
						<tr>
							<td colspan="4" style="padding:0px;" align="left">
							<table width="100%" >
							<tr>
								<th class="required"  width="150px">Telefone da Instituição:</th>
								<td width="170px">
									(<h:inputText readonly="#{solRevalidacaoDiploma.readOnly}" onkeyup="return formatarInteiro(this);"
									value="#{solRevalidacaoDiploma.obj.ddiTelefoneUniversidade}" maxlength="5"
									size="2" id="ddiTelefoneInstituicao" style="text-align:center" />) 
									(<h:inputText readonly="#{solRevalidacaoDiploma.readOnly}" onkeyup="return formatarInteiro(this);"
									value="#{solRevalidacaoDiploma.obj.dddTelefoneUniversidade}" maxlength="2"
									size="2" id="dddTelefoneInstituicao" style="text-align:center" />) 
									<h:inputText disabled="#{solRevalidacaoDiploma.readOnly}"
									value="#{solRevalidacaoDiploma.obj.telefoneUniversidade}" onkeyup="return formatarInteiro(this);"
									 maxlength="20" size="9"
									id="telefoneInstituicao" />
								</td>
								<th width="170px">Fax da Instituição:</th>
								<td>
									(<h:inputText readonly="#{solRevalidacaoDiploma.readOnly}" onkeyup="return formatarInteiro(this);"
									value="#{solRevalidacaoDiploma.obj.ddiFaxUniversidade}" maxlength="5"
									size="2" id="ddiFaxInstituicao" style="text-align:center" />) 
									(<h:inputText disabled="#{solRevalidacaoDiploma.readOnly}" onkeyup="return formatarInteiro(this);"
									value="#{solRevalidacaoDiploma.obj.dddfaxUniversidade}" maxlength="2"
									size="2" id="dddfaxInstituicao" style="text-align:center" />) 
									<h:inputText readonly="#{solRevalidacaoDiploma.readOnly}"
									value="#{solRevalidacaoDiploma.obj.faxUniversidade}" onkeyup="return formatarInteiro(this);"
									 maxlength="20" size="9"
									id="faxInstituicao" />
								</td>
							</tr>
							</table>
							</td>	
						</tr>
						</table>
					</td>
				</tr>	


				<tr>
					<th ></th>
					<td>
						
					</td>
				</tr>
			

				<!-- ============= AGENDAMENTO ============ -->
				<c:if test="${not empty todasDatas}">
				<tr>
					<td colspan="4" style="padding:0px;" align="left">
					<table width="100%" class="subFormulario">
						<caption id="dataHorario">Datas e Horários Disponíveis para Agendamento</caption>
						<tr>
							<th  width="160"  class="required">Data:</th>
							<td   width="120px">
								<a4j:region>
									<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroData}"	
										id="dataAgendamento"	valueChangeListener="#{solRevalidacaoDiploma.carregarHorarios}">
										<f:selectItem itemLabel="Selecione" itemValue=""/>
										<f:selectItems value="#{todasDatas}" />
										<a4j:support event="onchange" reRender="horarioAgendamento" />
									</h:selectOneMenu>
									<a4j:status>
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif" />
										</f:facet>
									</a4j:status>
								</a4j:region>
							</td>
							<th class="required" width="197">Horário:</th>
							<td>
								<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma.id}"
									id="horarioAgendamento" >
									<f:selectItem itemLabel="Selecione" itemValue="0" />
									<f:selectItems value="#{solRevalidacaoDiploma.horariosData}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			
			</tbody>
				<tfoot>
					<tr>
						<td colspan="4" align="center">
							<h:commandButton	value="#{solRevalidacaoDiploma.confirmButton}" action="#{solRevalidacaoDiploma.cadastrarPublico}" id="submeter" />
							 &nbsp; 
							<h:commandButton value="Cancelar" immediate="true"
							action="#{solRevalidacaoDiploma.cancelarPublic}" id="cancelarOperacao"	onclick="#{confirm}" />
						</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
	
			<br>
			<center>
				<html:img page="/img/required.gif" style="vertical-align: top;" />
				 <span class="fontePequena">Campos de preenchimento obrigatório. </span> 
				<br>
				<br>
			</center>
		
	
</f:view>
<script type="text/javascript">
	var posProcessamento = function() {
	
	};

	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',
			'form:endBairro', 'form:endMunicipio', 'form:ufEnd', posProcessamento);
</script>


<%@include file="/public/include/rodape.jsp"%>