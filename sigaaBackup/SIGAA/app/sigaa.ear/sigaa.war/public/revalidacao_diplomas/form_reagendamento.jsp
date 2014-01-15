<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>

<style>
	#cepIndicator {padding: 0 25px;color: #999;}
	.descricaoOperacao {font-size: 1.2em;}
	.descricaoOperacao p {text-align: justify;}
	h3,h4 {font-variant: small-caps;text-align: center;margin: 2px 0 20px;}
	h4 {margin: 15px 0 20px;}
	#dataHorario {}
</style>

<f:view>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />
	<c:set var="todasDatas" value="#{solRevalidacaoDiploma.allComboData}" />
	<h2>Ficha de Inscri��o para <h:outputText value="#{solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.titulo}"/></h2>
	<div class="descricaoOperacao">
		<p>
			<ul>
				<li>
					O per�odo para inscri��o na WEB ser� no per�odo de
					<ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.inicioInscricao}"/> �
					<ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.finalInscricao}"/>.
				</li> 
				<li>
					Preencha o formul�rio de inscri��o para agendar a entrega dos documentos, para data compreendida entre 
					<ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.inicioAgenda}"/> e 
					<ufrn:format type="data" valor="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.finalAgenda}"/>
					 excluindo s�bados, domingo e feriados.
				</li>
			</ul>	
		</p>
		<c:if test="${empty todasDatas}">
		<p>
			<b>Informamos que seu cadastro ser� adicionado a uma lista de espera e ap�s o dia  ${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.periodoReagendamento}
			 ser� disponibilizado novas datas e hor�rios.</b>
		</p>
		</c:if>
		<h4>
			<a	href="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.urlEdital}">
			Clique Aqui Para maiores informa��es veja o edital 
			</a>
		</h4>
	</div>

	<h:form id="form">

		<table class="visualizacao" width="98%">
			<!-- ============= DADOS PESSOAIS ============ -->
			<caption>Dados Pessoais</caption>
			<tbody>

				<tr>
					<th width="15%">Nome:</th>
					<td colspan="3">
						<h:outputText value="#{solRevalidacaoDiploma.obj.nome}" id="nome" />
					</td>
				</tr>

				<tr>
					<th>Nome da M�e:</th>
					<td colspan="3"><h:outputText
						value="#{solRevalidacaoDiploma.obj.nomeMae}" id="mae" /></td>
				</tr>

				<tr>
					<th>Nome do Pai:</th>
					<td colspan="3">
						<h:outputText value="#{solRevalidacaoDiploma.obj.nomePai}" id="pai" />
					</td>
				</tr>
				<!-- ============= NACIONALIDADE ============ -->
				
				<tr>
					<td colspan="4">
						<table width="100%">
							<tr>
								<th width="15%">Nacionalidade:</th>
								<td width="24%">
									<h:outputText value="#{solRevalidacaoDiploma.obj.pais.nome}" id="nacionalidade"	/>
								</td>
					
								<th  width="10%">Naturalidade:</th>
								<td width="50%">
									<h:outputText value="#{solRevalidacaoDiploma.obj.natural}" id="natural"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>		
				
				<!-- ============= ENDERE�O DE CONTATO ============ -->
				<tr>
					<td colspan="4">
					<table width="100%">
						<tr class="linhaCep">
							<th width="15%">CEP:</th>
							<td colspan="5">
								<h:outputText value="#{solRevalidacaoDiploma.obj.cep}"id="endCEP"/>
							</td>
						</tr>

						<tr>
							<th>UF: <span></span></th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.unidadeFederativa.descricao}" id="ufEnd"	/>
							</td>
							<th >Munic�pio:</th>
							<td colspan="4">
								<h:outputText value="#{solRevalidacaoDiploma.obj.municipio.nome}" id="endMunicipio" />
							</td>
						</tr>

						<tr>
							<td colspan="5">
							<table width="100%">
							<tr>
								<th width="16%">Logradouro:</th>
								<td colspan="3">
									<h:outputText value="#{solRevalidacaoDiploma.obj.tipoLogradouro.descricao}"
										 id="tipoLogradouro"/>
										
									&nbsp;
									<h:outputText value="#{solRevalidacaoDiploma.obj.logradouro }"	id="logradouro" />
								</td>
								<th width="10px">N.&deg;:</th>
								<td>
									<h:outputText value="#{solRevalidacaoDiploma.obj.numero}" id="endNumero" />
								</td>
							</tr>
							</table>
							</td>
						</tr>		

						<tr>
							<td colspan="5">
							<table width="100%">
							<tr>
							
							<th width="16%">Bairro:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.bairro}" id="endBairro" />
							</td>
							<th>Telefone:</th>
							<td colspan="3">
								(<h:outputText value="#{solRevalidacaoDiploma.obj.telefone_ddd}"  id="dddFixoNumero" />) 
								<h:outputText value="#{solRevalidacaoDiploma.obj.telefone}"	id="telFixoNumero" />
							</td>
							</tr>
							</table>
							</td>
						</tr>	
						<tr>
							<th >Fax:</th>
							<td colspan="5">
								<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.fax}" value="(#{solRevalidacaoDiploma.obj.fax_ddd})" id="dddFax" /> 
								<h:outputText value="#{solRevalidacaoDiploma.obj.fax}" id="faxNumero" />
							</td>
						</tr>
						<tr>
							<td colspan="5">
								<table width="100%">
								<tr>
									<th width="15%">E-mail:</th>
									<td width="10%">
										<h:outputText value="#{solRevalidacaoDiploma.obj.email}" id="confirmaEmail"/>
									</td>
									<th></th>
									<td></td>
		
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
				<!-- ============= DOCUMENTA��O ============ -->
				<tr>
					<td colspan="4">
					<table width="100%" class="subVizualizacao">
						<caption>Documenta��o</caption>

						<t:htmlTag value="tbody" id="documento">
							<c:if test="${solRevalidacaoDiploma.nacionalidadePadrao}">
							<tr>
								<th  width="16%">RG:</th>
								<td width="10%"><h:outputText value="#{solRevalidacaoDiploma.obj.rgNumero}" id="rg" /></td>
								<th >�rg�o de Expedi��o:</th>
								<td>
									<h:outputText value="#{solRevalidacaoDiploma.obj.rgOrgaoExpedicao}"  id="orgaoExpedicao"/>
								</td>
								<th>Data de Expedi��o:</th>
								<td>
									<h:outputText value="#{solRevalidacaoDiploma.obj.rgDataExpedicao}" id="dataExpedicao"/>
								</td>
							</tr>
							</c:if>
							<tr>
								<c:choose>
									<c:when test="${solRevalidacaoDiploma.nacionalidadePadrao}">
										<th align="right">CPF:</th>
										<td align="left" colspan="4">
											<h:outputText value="#{solRevalidacaoDiploma.obj.cpf}"/>
										</td>
									</c:when>
									<c:otherwise>
										<th align="right">N� Passaporte:</th>
										<td align="left"  colspan="4"><h:outputText
											value="#{solRevalidacaoDiploma.obj.passaporte}" id="passaporte"/></td>
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
				
					<!-- ============= DADOS DA INSTITUI��O ============ -->
				<tr>
					<td colspan="4">
						<table width="100%" class="subVisualizacao">
						<caption>Dados da Institui��o Emissora do Diploma</caption>
						<tr>
							<th width="16%">Nome:</th>
							<td colspan="3">
								<h:outputText value="#{solRevalidacaoDiploma.obj.universidade}"
								id="universidade" />
							</td>
			
						</tr>
						<tr>
							<th width="16%">Curso:</th>
							<td width="9%">
								<h:outputText value="#{solRevalidacaoDiploma.obj.curso}"id="curso"  />
							</td>
							<th width="15%">Ano de Conclus�o:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.anoConclusao}"	id="anoConclusao"/>
							</td>
						</tr>
						<tr>
							<th>Endere�o:</th>
							<td>	
								<h:outputText value="#{solRevalidacaoDiploma.obj.logradouroUniversidade}" id="logradouroUniversidade"/>
							</td>
							<th>N�:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.numeroUniversidade}" id="numeroUniversidade"  />
							</td>
						</tr>
						<tr>
							<td colspan="4">
							<table width="100%">
							<tr>
								<th width="16%">Pa�s:</th>
								<td width="20%">
									<h:outputText value="#{solRevalidacaoDiploma.obj.paisUniversidade.nome}" id="paisUniversidade"/>
								</td>
								<th>Cidade:</th>
								<td>
									<h:outputText value="#{solRevalidacaoDiploma.obj.cidadeUniversidade}" id="cidadeUniversidade" />
								</td>
							</tr>
							</table>
							</td>
						</tr>	
						<tr>
							<th>P�gina Eletr�nica:</th>
							<td  colspan="3">
								<h:outputText value="#{solRevalidacaoDiploma.obj.paginaUniversidade}"
								id="paginaUniversidade"  />
							</td>
						</tr>
						<tr>
							<td colspan="4">
							<table width="100%">
							<tr>
								<th width="16%">E-mail da Institui��o:</th>
								<td  width="20%">
									<h:outputText value="#{solRevalidacaoDiploma.obj.emailUniversidade}" id="emailUniversidade"  />
								</td>
								<th></th>
								<td></td>
							</tr>	
							</table>
							</td>
						</tr>		
							
						<tr>
							<td colspan="4">
							<table width="100%">
							<tr>
								<th  width="16%">Telefone da Institui��o:</th>
								<td  width="18%">
									<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.telefoneUniversidade}" value="(#{solRevalidacaoDiploma.obj.dddTelefoneUniversidade})"/> 
									<h:outputText  value="#{solRevalidacaoDiploma.obj.telefoneUniversidade}" id="telefoneInstituicao" />
								</td>
								<th>Fax da Institui��o:</th>
								<td>
									<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.faxUniversidade}"
									value="(#{solRevalidacaoDiploma.obj.dddfaxUniversidade})" id="dddfaxInstituicao"  /> 
									<h:outputText
									value="#{solRevalidacaoDiploma.obj.faxUniversidade}" id="faxInstituicao" />
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
					<td>
						
					</td>
				</tr>
				<!-- ============= AGENDAMENTO ============ -->
				<c:if test="${not empty todasDatas}">
				<tr>
					<td colspan="4">
					<table width="100%" class="subFormulario">
						<caption id="dataHorario">Datas e hor�rios Dispon�veis para Agendamento</caption>
						<tr>
							<th width="13%" class="required" >Data:</th>
							<td width="15%">
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
							<th width="10%" class="required" >Hor�rio:</th>
							<td>
								<h:selectOneMenu value="#{solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma.id}"	id="horarioAgendamento" >
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
						<h:commandButton value="#{solRevalidacaoDiploma.confirmButton}" action="#{solRevalidacaoDiploma.cadastrarReagendamento}" id="submeter" />
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
		 <span class="fontePequena">Campos de preenchimento obrigat�rio. </span> 
		<br>
		<br>
	</center>

</f:view>

<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
		$('form:ufEnd').onchange();
	} );
</script>


<%@include file="/public/include/rodape.jsp"%>