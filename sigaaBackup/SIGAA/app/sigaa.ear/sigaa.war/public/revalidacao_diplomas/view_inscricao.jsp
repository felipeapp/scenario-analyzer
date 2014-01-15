<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>

<style>
#cepIndicator {padding: 0 25px;color: #999;}
.descricaoOperacao {font-size: 1.2em;}
.descricaoOperacao p {text-align: justify;}
h3,h4 {font-variant: small-caps;text-align: center;margin: 2px 0 20px;}
h4 {margin: 15px 0 20px;}
.vermelho{color: #FF1111;}
.declaracao{padding: 15px 25px !important;text-align: justify !important;}
.separador{border-top: 1px solid #EBEBEB}
</style>

<f:view>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />

	<h2>Dados do Inscrito no <h:outputText value="#{solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.titulo}"/></h2>
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
					<th>Nome da Mãe:</th>
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
				
				<!-- ============= ENDEREÇO DE CONTATO ============ -->
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
							<th >Município:</th>
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
				<!-- ============= DOCUMENTAÇÃO ============ -->
				<tr>
					<td colspan="4">
					<table width="100%" class="subVisualizacao">
						<caption>Documentação</caption>

						<t:htmlTag value="tbody" id="documento">
							<c:if test="${solRevalidacaoDiploma.nacionalidadePadrao}">
							<tr>
								<th  width="16%">RG:</th>
								<td width="10%"><h:outputText value="#{solRevalidacaoDiploma.obj.rgNumero}" id="rg" /></td>
								<th >Órgão de Expedição:</th>
								<td>
									<h:outputText value="#{solRevalidacaoDiploma.obj.rgOrgaoExpedicao}"  id="orgaoExpedicao"/>
								</td>
								<th>Data de Expedição:</th>
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
											<h:outputText converter="convertCpf" value="#{solRevalidacaoDiploma.obj.cpf}"/>
										</td>
									</c:when>
									<c:otherwise>
										<th align="right">Nº Passaporte:</th>
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
				
					<!-- ============= DADOS DA INSTITUIÇÃO ============ -->
				<tr>
					<td colspan="4">
						<table width="100%" class="subVisualizacao">
						<caption>Dados da Instituição Emissora do Diploma</caption>
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
							<th width="15%">Ano de Conclusão:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.anoConclusao}"	id="anoConclusao"/>
							</td>
						</tr>
						<tr>
							<th>Endereço:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.logradouroUniversidade}" id="logradouroUniversidade"/>
							</td>
							<th>Nº:</th>
							<td>
								<h:outputText value="#{solRevalidacaoDiploma.obj.numeroUniversidade}" id="numeroUniversidade"  />
							</td>
						</tr>
						<tr>
							<td colspan="4">
							<table width="100%">
							<tr>
								<th width="16%">País:</th>
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
							<th>Página Eletrônica:</th>
							<td  colspan="3">
								<h:outputText value="#{solRevalidacaoDiploma.obj.paginaUniversidade}"
								id="paginaUniversidade"  />
							</td>
						</tr>
						<tr>
							<td colspan="4">
							<table width="100%">
							<tr>
								<th width="16%">E-mail da Instituição:</th>
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
								<th  width="16%">Telefone da Instituição:</th>
								<td  width="18%">
								<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.telefoneUniversidade}" value="(#{solRevalidacaoDiploma.obj.ddiTelefoneUniversidade})"/>
									<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.telefoneUniversidade}" value="(#{solRevalidacaoDiploma.obj.dddTelefoneUniversidade})"/> 
									<h:outputText  value="#{solRevalidacaoDiploma.obj.telefoneUniversidade}" id="telefoneInstituicao" />
								</td>
								<th>Fax da Instituição:</th>
								<td>
									<h:outputText rendered="#{not empty solRevalidacaoDiploma.obj.faxUniversidade}"
									value="(#{solRevalidacaoDiploma.obj.ddiFaxUniversidade})" id="ddifaxInstituicao"  /> 
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
				<tr>
					<td colspan="4">
						<c:choose>
							<c:when test="${not empty solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma
								&& not empty solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma.data }">
								<table width="100%" class="subVisualizacao">
									<caption id="dataHorario">Datas e Horários Agendado</caption>
									<tr>
										<th width="16%" >Data:</th>
										<td width="15%">
											<h:outputText value="#{solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma.data}"/>
										</td>
										<th width="10%" >Horário:</th>
										<td>
											<h:outputText value="#{solRevalidacaoDiploma.obj.agendaRevalidacaoDiploma.horario}"/>
										</td>
									</tr>
								</table>
							</c:when>
							<c:otherwise>	
									Caso os campos data e horário de agendamento não estejam disponíveis, 
									o inscrito aguardará um novo período para seu reagendamento.
							</c:otherwise> 
						</c:choose>
					</td>
				</tr>
				<tr>
					<td colspan="6"  class="separador" >
					&nbsp;
					</td>
				</tr>	
				<tr>
					<td colspan="4" class="declaracao">
						<h4>DECLARAÇÃO DO REQUERENTE:</h4>
						Atesto que todas as informações prestadas são verdadeiras e ser minha a inteira responsabilidade de 
						entrega de todos os documentos exigidos, conforme relação anexa. Atesto, também, estar ciente de que qualquer 
						irregularidade ou ausência de documentos na forma exigida verificada após o protocolo, o processo será <b>automaticamente 
						indeferido</b> e ter conhecimento de que, em nenhuma circunstância, será devolvida a taxa do processo de Revalidação de Diploma.
						<p>Declaro que estou ciente e concordo com os procedimentos e normas estabelecidas pela ${ configSistema['siglaInstituicao'] } para o processo de Revalidação 
						de Diploma, que ora me submeto.
						</p>
					</td>
				</tr>
				<tr>
					<td colspan="4">	
						&nbsp;
					</td>
				</tr>

				<tr>
					<td colspan="4" class="declaracao">	
					Natal,  _____/_______/___________
					</td>
				</tr>
				<tr>
					<td colspan="4" class="declaracao">
					 __________________________________________________________<br/>
                  	 Assinatura
					</td>
				</tr>
				<tr>
					<td colspan="4">	
						&nbsp;
					</td>
				</tr>
			</tbody>
				<tfoot>
					<tr>
						<td colspan="4" align="center">
							<input type="button"  onclick="print();" value="Imprimir" /> 
							
						</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
	
		

	
</f:view>

<%@include file="/public/include/rodape.jsp"%>