<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean"%>
<%-- Pagina de busca padrao para os usuario da biblioteca                              --%>
<%-- A idéia é que onde o usuario precisar buscar um usuario essa pagina seja incluida --%>
<%-- e todas as acoes com o usuario comecem a partir dessa pagina                      --%>


<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	<a4j:keepAlive beanName="cadastroUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="bloquearUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="listaEmprestimosAtivosUsuarioMBean" />
	<a4j:keepAlive beanName="listaRenovacoesAtivasUsuarioMBean" />
	<a4j:keepAlive beanName="listaDevolucoesRecentesUsuarioMBean" />
	<a4j:keepAlive beanName="emiteHistoricoEmprestimosMBean" />
	<a4j:keepAlive beanName="usuarioExternoBibliotecaMBean" />
	<a4j:keepAlive beanName="verificaSituacaoUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="verificaVinculosUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="suspensaoUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />
	<a4j:keepAlive beanName="moduloCirculacaoMBean" />
	<a4j:keepAlive beanName="enviaMensagemUsuariosBibliotecaMBean" />
	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="desfazQuitacaoVinculoUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="emiteTermoAdesaoBibliotecaMBean" />


	<h:form id="formBuscaUsuarioBiblioteca">

		<c:set var="BUSCA_USUARIO_COMUM" value="<%= BuscaUsuarioBibliotecaMBean.RADIO_BUTTON_BUSCA_USUARIO_COMUM %>" scope="request" />
		<c:set var="BUSCA_USUARIO_EXTERNO" value="<%= BuscaUsuarioBibliotecaMBean.RADIO_BUTTON_BUSCA_USUARIO_EXTERNO %>" scope="request" />
		<c:set var="BUSCA_BIBLIOTECA" value="<%= BuscaUsuarioBibliotecaMBean.RADIO_BUTTON_BUSCA_BIBLIOTECA %>" scope="request" />

		<h2> <ufrn:subSistema /> &gt; Buscar Usuários da Biblioteca  &gt; ${buscaUsuarioBibliotecaMBean.nomeOperacao} </h2>
		
		<div class="descricaoOperacao">
			<p> Página de pesquisa de usuários da biblioteca. </p>
			<p>	A partir dos resultados é possível realizar várias operações, como verificar a situação dos usuários, emitir a sua declaração de quitação, estornar um empréstimo do usuário, entre outras.</p>
			<p><strong>Usuário Comum</strong> é todo discente, docente ou servidor.</p>
		</div>
		
		
			<table class="formulario" style="width:80%;">
				<caption> Informe os critérios de busca</caption>
			
				<tbody>
					<tr>
						<th colspan="2">Tipo de Usuário:</th>
						<td>
							<a4j:region>
								<h:selectOneRadio value="#{buscaUsuarioBibliotecaMBean.valorRadioButtonTipoUsuario}" onclick="submit();"  
									id="tipoBusca"	valueChangeListener="#{buscaUsuarioBibliotecaMBean.trocaTipoUsuarioBusca}">
								
									<c:if test="${buscaUsuarioBibliotecaMBean.buscaUsuarioComumHabilitada}">
										<f:selectItem itemLabel="Usuário Comum" itemValue="1" />
									</c:if>
									
									<c:if test="${buscaUsuarioBibliotecaMBean.buscaUsuarioExternoHabilitada}">
										<f:selectItem itemLabel="Usuário Externo" itemValue="2" />
									</c:if>
									
									<c:if test="${buscaUsuarioBibliotecaMBean.buscaBibliotecaHabilitada}">
										<f:selectItem itemLabel="Biblioteca" itemValue="3" />
									</c:if>
									
								</h:selectOneRadio>
							</a4j:region>
						</td>
					</tr>
			
					<c:if test="${buscaUsuarioBibliotecaMBean.valorRadioButtonTipoUsuario == BUSCA_USUARIO_COMUM}">
						
						<tr>
							<td style="width:20px;">
								<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarMatriculaUsuario}" styleClass="noborder" id="chkMatriculaUsuario" onclick="selecionar('matricula', true);" />
							</td>
							<th style="text-align:left;">Matrícula:</th>
							<td> 
								<h:inputText
									value="#{buscaUsuarioBibliotecaMBean.matriculaUsuario}" size="14" id="matriculaUsuario" maxlength="10"
									onkeyup="return formatarInteiro(this);"
									onfocus="selecionar('matricula', false);" />
									<ufrn:help>Para Alunos em geral: Infantil, Médio/Técnico, Graduação, Pós-Graduação, etc... </ufrn:help>
							</td>
						</tr>
						
						<tr>
							<td style="width:20px;">
								<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarSiapeUsuario}" styleClass="noborder" id="chkMSiapeUsuario" onclick="selecionar('siape', true);" />
							</td>
							<th style="text-align:left;">Siape:</th>
							<td> 
								<h:inputText
									value="#{buscaUsuarioBibliotecaMBean.siapeUsuario}" size="14" id="siapeUsuario" maxlength="10"
									onkeyup="return formatarInteiro(this);"
									onfocus="selecionar('siape', false);" />
									<ufrn:help>Para Servidores Técnicos Administrativos ou Professores.</ufrn:help>
							</td>
						</tr>
						
						<tr>
							<td>
								<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarCpfUsuario}" styleClass="noborder" id="chkCpfUsuario" onclick="selecionar('cpf', true);" />
							</td>
							<th style="text-align:left;">CPF:</th>
							<td>	
								<h:inputText value="#{buscaUsuarioBibliotecaMBean.cpfUsuario}" size="14" maxlength="14"
														id="cpfUsuario"
														onblur="formataCPF(this, event, null)"
														onkeypress="return formataCPF(this, event, null)"
														onfocus="selecionar('cpf', false);" >
									
								</h:inputText>
							</td>
						</tr>
						
						<tr>
							<td>
								<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarPassaporte}" styleClass="noborder" id="chkPassaporteUsuario" onclick="selecionar('passaporte', true);" />
							</td>
							<th style="text-align:left;">Passaporte:</th>
							<td>	
								<h:inputText value="#{buscaUsuarioBibliotecaMBean.passaporteUsuario}" size="14" maxlength="14"
														id="passaporteUsuario"
														onfocus="selecionar('passaporte', false);" >
								</h:inputText>
								<ufrn:help>Para usuários extrangeiros que não possuem CPF</ufrn:help>
							</td>
						</tr>
						
						
						
						<tr>
							<td style="width:20px;">
								<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarNomeUsuario}" styleClass="noborder" id="chkNomeUsuario" />
							</td>
							<th style="text-align:left;width:110px;">Nome:</th>
							<td> 
								<h:inputText
									value="#{buscaUsuarioBibliotecaMBean.nomeUsuario}" size="60" id="nomeUsuario" maxlength="100"
									onkeyup="CAPS(this)"
									onfocus="selecionar('nome', false);" >						
								</h:inputText>
							</td>
						</tr>
						
					</c:if>
					
					
					
					
					
					
					
					
					<c:if test="${buscaUsuarioBibliotecaMBean.valorRadioButtonTipoUsuario == BUSCA_USUARIO_EXTERNO}">
					
						<c:set var="tipoUsuarioUsuarioExterno" value="1" />
					
						
							<tr>
								<td style="width:20px;">
									<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarCpfUsuarioExterno}" styleClass="noborder" id="chkCpfUE" onclick="selecionar('cpfUE', true);" />
								</td>
								<th style="text-align:left;width:110px;">CPF:</th>
								<td>
									<h:inputText value="#{buscaUsuarioBibliotecaMBean.cpfUsuarioExterno}" size="14" maxlength="14"
															id="cpfUE"
															onblur="formataCPF(this, event, null)"
															onkeypress="return formataCPF(this, event, null)"
															onfocus="selecionar('cpfUE', false);" >
										</h:inputText>
										
										
										
								</td>
							</tr>
							
							<tr>
								<td>
									<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarPassaporteUsuarioExterno}" styleClass="noborder" id="chkPassaporteUE" onclick="selecionar('passaporteUE', true);" />
								</td>
								<th style="text-align:left;">Passaporte:</th>
								<td>	
									<h:inputText value="#{buscaUsuarioBibliotecaMBean.passaporteUsuarioExterno}" size="14" maxlength="14"
															id="passaporteUE"
															onfocus="selecionar('passaporteUE', false);" >
									</h:inputText>
									<ufrn:help>Para usuários extrangeiros que não possuem CPF</ufrn:help>
								</td>
							</tr>
							         
						
						
							<tr>
								<td>
									<h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarNomeUsuarioExterno}" styleClass="noborder" id="chkNomeUE" />
								</td>
								<th style="text-align:left;">Nome:</th>
								<td><h:inputText value="#{buscaUsuarioBibliotecaMBean.nomeUsuarioExterno}" size="60" id="nomeUE" maxlength="100" onkeyup="CAPS(this)" onfocus="selecionar('nomeUE', false);" /></td>
							</tr>
							
					</c:if>
					
					
					
					
					
					
					<c:if test="${buscaUsuarioBibliotecaMBean.valorRadioButtonTipoUsuario == BUSCA_BIBLIOTECA}">
						<tr>
							<td style="width:20px;"><h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarBibliotecaInterna}" styleClass="noborder" id="chkBiblioteca" onclick="selecionar('biblioteca', false);" /></td>
							<th style="text-align:left;width:110px;">Biblioteca:</th>
							<td>
								<h:selectOneMenu value="#{buscaUsuarioBibliotecaMBean.biblioteca.id}" onfocus="selecionar('biblioteca', true);" id="biblioteca" >
									<f:selectItem itemLabel="-- Selecione --" itemValue="0" />
									<f:selectItems value="#{buscaUsuarioBibliotecaMBean.bibliotecasInternas}" />
								</h:selectOneMenu>
							</td>
						</tr>
							
						<tr>
							<td><h:selectBooleanCheckbox value="#{buscaUsuarioBibliotecaMBean.buscarBibliotecaExterna}" styleClass="noborder" id="chkBibliotecaExterna" onclick="selecionar('bibliotecaExterna', false);" /></td>
							<th style="text-align:left;">Biblioteca Externa:</th>
							<td>
								<h:selectOneMenu value="#{buscaUsuarioBibliotecaMBean.bibliotecaExterna.id}" onfocus="selecionar('bibliotecaExterna', true);" id="bibliotecaExterna">
									<f:selectItem itemLabel="-- Selecione --" itemValue="0" />
									<f:selectItems value="#{buscaUsuarioBibliotecaMBean.bibliotecasExternas}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</c:if>
					
				</tbody>
			
				<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton action="#{buscaUsuarioBibliotecaMBean.buscarUsuario}" value="Buscar" rendered="true" id="btnBusca" />
							<h:commandButton action="#{buscaUsuarioBibliotecaMBean.cancelar}" value="Cancelar" rendered="true" onclick="#{confirm}" id="btnCancelar" />
						</td>
					</tr>
				</tfoot>
			</table>
	
		<%--   Lista com os resultados da busca   --%>

		<c:if test="${buscaUsuarioBibliotecaMBean.paginaAcoesExtras != null}">
			<c:import url="${buscaUsuarioBibliotecaMBean.paginaAcoesExtras}" />
		</c:if>

		<%@include file="/biblioteca/circulacao/resultadoBuscaUsuarioBiblioteca.jsp"%>
		

	</h:form>
	
</f:view>


<script type="text/javascript">

		function selecionar(campo, clicouCheckBox){
			switch (campo){
				case "matricula":

					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = true;
					}
					
					getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = false;
					
					getEl('formBuscaUsuarioBiblioteca:cpfUsuario').dom.value =  "";
					getEl('formBuscaUsuarioBiblioteca:siapeUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:passaporteUsuario').dom.value =  "";
				break;
				
				case "cpf":

					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = true;
					}
					
					getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = false;
					
					getEl('formBuscaUsuarioBiblioteca:matriculaUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:siapeUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:passaporteUsuario').dom.value =  "";
				break;

				case "passaporte":

					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = true;
					}
					
					getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = false;

					getEl('formBuscaUsuarioBiblioteca:matriculaUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:siapeUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:cpfUsuario').dom.value =  "";
				break;
				
				
				

				case "nome":
					getEl('formBuscaUsuarioBiblioteca:chkNomeUsuario').dom.checked = true;
					
				break;
				case "siape":

					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkMSiapeUsuario').dom.checked = true;
					}
					
					getEl('formBuscaUsuarioBiblioteca:chkMatriculaUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkCpfUsuario').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:chkPassaporteUsuario').dom.checked = false;

					getEl('formBuscaUsuarioBiblioteca:matriculaUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:cpfUsuario').dom.value = "";
					getEl('formBuscaUsuarioBiblioteca:passaporteUsuario').dom.value =  "";
				break;


				 
				
				case "cpfUE":

					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkCpfUE').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkCpfUE').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkCpfUE').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkCpfUE').dom.checked = true;
					}
					
					getEl('formBuscaUsuarioBiblioteca:chkPassaporteUE').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:passaporteUE').dom.value =  "";
				break;

				case "passaporteUE":
					
					if(clicouCheckBox){
						if(getEl('formBuscaUsuarioBiblioteca:chkPassaporteUE').dom.checked)
							getEl('formBuscaUsuarioBiblioteca:chkPassaporteUE').dom.checked = true;
						else
							getEl('formBuscaUsuarioBiblioteca:chkPassaporteUE').dom.checked = false;
					}else{
						getEl('formBuscaUsuarioBiblioteca:chkPassaporteUE').dom.checked = true;
					}

					
					getEl('formBuscaUsuarioBiblioteca:chkCpfUE').dom.checked = false;
					getEl('formBuscaUsuarioBiblioteca:cpfUE').dom.value =  "";
					
				break;
				
				
				case "nomeUE":
					getEl('formBuscaUsuarioBiblioteca:chkNomeUE').dom.checked = true;
				break;



				
				case "biblioteca":
					
					getEl('formBuscaUsuarioBiblioteca:chkBiblioteca').dom.checked = true;
					getEl('formBuscaUsuarioBiblioteca:chkBibliotecaExterna').dom.checked = false;

					getEl('formBuscaUsuarioBiblioteca:bibliotecaExterna').dom.value = "0";
				break;

				case "bibliotecaExterna":
					
					getEl('formBuscaUsuarioBiblioteca:chkBibliotecaExterna').dom.checked = true;
					getEl('formBuscaUsuarioBiblioteca:chkBiblioteca').dom.checked = false;

					getEl('formBuscaUsuarioBiblioteca:biblioteca').dom.value = "0";
				break;
			}
		}
	</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

