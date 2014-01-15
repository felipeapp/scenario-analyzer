<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="registraConsultasDiariasMateriaisMBean" />

	<h:form id="formRegistraConsultasMateriais">
	
		<h2> <ufrn:subSistema/> &gt; Cadastrar Consultas Locais </h2>
		
		<div class="descricaoOperacao" style="width:80%;">
			<p>Utilize este formulário para registra as consultas de materiais realizadas na biblioteca.</p>
			<ul>
				<li>Selecione a Data da Consulta</li>
				<li>Selecione a Biblioteca onde ocorreram as consultas</li>
				<li>Selecione a Coleção do material consultado</li>
				<li>Selecione o Tipo do material consultado</li>
				<li>Digite as informações da quantidade de materiais consultados por classificação e clique em <strong>Adicionar</strong>.</li>
			</ul>
			<p>Após inserir todas as classes consultadas do dia, clique em <strong>Cadastrar</strong></p>
		</div>
		
		
		<table class="formulario" width="80%">
			<caption>Cadastrar material consultado</caption>
			
			<tbody>
				
				<%-- Formulário no qual o usuário escolhe os dados do registro--%>
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width:100%;">
							<caption>Dados do Registro</caption>
						
						<tr>
							<th class="obrigatorio">Data da Consulta:</th>
							<td>
								<t:inputCalendar value="#{registraConsultasDiariasMateriaisMBean.obj.dataConsulta}" id="dataConsulta"
										renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)"
										size="10" popupDateFormat="dd/MM/yyyy" maxlength="10"
										onkeyup="return document.getElementById('form:exibirClasses').click();"/>
							</td>
						</tr>
						
						<tr>
							<th class="obrigatorio">Biblioteca:</th>
							<td>					
								<h:selectOneMenu id="comboBoxBibliotecas" value="#{registraConsultasDiariasMateriaisMBean.obj.biblioteca.id}"
										onchange="document.getElementById('formRegistraConsultasMateriais:exibirClasses').click();">
									<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{registraConsultasDiariasMateriaisMBean.bibliotecasInternas}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						
						<tr>
							<th class="obrigatorio">Tipo de Material:</th>
						
							<td>					
								<h:selectOneMenu id="comboBoxTiposMateriais" value="#{registraConsultasDiariasMateriaisMBean.obj.tipoMaterial.id}"
										onchange="document.getElementById('formRegistraConsultasMateriais:exibirClasses').click();">
									<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{registraConsultasDiariasMateriaisMBean.tiposMaterial}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						
						<tr>
							<th class="obrigatorio">Coleção:</th>
						
							<td>					
								<h:selectOneMenu  id="comboBoxColecoes" value="#{registraConsultasDiariasMateriaisMBean.obj.colecao.id}"
										onchange="document.getElementById('formRegistraConsultasMateriais:exibirClasses').click();">
									<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{registraConsultasDiariasMateriaisMBean.colecoes}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						
						<tr>
							<th>Turno:</th>
							<td>
								<h:selectOneRadio  id="turnoRegisto" value="#{registraConsultasDiariasMateriaisMBean.obj.turno}"
										onclick="document.getElementById('formRegistraConsultasMateriais:exibirClasses').click();">
									<f:selectItem itemLabel="Matutino" itemValue="1" />
									<f:selectItem itemLabel="Vespertino" itemValue="2" />
									<f:selectItem itemLabel="Noturno" itemValue="3" />
								</h:selectOneRadio>
							</td>
						</tr>
						</table>
						
					</td>
					
				</tr>
				
				<%-- 
				-- Exibe a listagem com as classes já registradas no banco 
				--%>
				<c:if test="${not empty registraConsultasDiariasMateriaisMBean.obj.classesConsultadas}">  
					<tr>
						<td colspan="2">
							<div class="infoAltRem" style="margin-bottom:0px;width:90%">
								<img src="${ctx}/img/delete.gif">: Remover Registros da Consulta
							</div>
								
							<table class="listagem" style="width:90%; margin-bottom: 20px;">
								<caption>Consultas já Registradas</caption>
								<thead>
									<tr>
										<th style="text-align:center;">Classe</th>
										<th style="text-align:center;">Quantidade</th>
										<th style="width:20px;"></th>
									</tr>
								</thead>
								
								
								<c:forEach var="c" items="#{registraConsultasDiariasMateriaisMBean.obj.classesConsultadas}">
									<tr>
										<c:if test="${ not empty c.classificacao1 }">
											<td style="text-align:center;">${c.classificacao1} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao1} </sub></sub></td>
										</c:if>
										<c:if test="${ not empty c.classificacao2 }">
											<td style="text-align:center;">${c.classificacao2} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao2}</sub></sub></td>
										</c:if>
										<c:if test="${ not empty c.classificacao3 }">
											<td style="text-align:center;">${c.classificacao3} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao3}</sub></sub></td>
										</c:if>
										<td style="text-align:center;">${c.quantidade}</td>
										<td>
											<h:commandLink action="#{registraConsultasDiariasMateriaisMBean.removerClasseRegistrada}"
													onclick="if (!confirm('Confirma a remoção dos dados da consulta? Eles já estão salvos no sistema.')) return false;"
													title="Remover registro" id="btnRemoverRegistroJahRegistrado">
												<h:graphicImage value="/img/delete.gif" alt="Remover registro" />
												<f:param name="idClasse" value="#{c.id}" />
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</c:if> 
				<c:if test="${empty registraConsultasDiariasMateriaisMBean.obj.classesConsultadas}">
					<tr>
						<td colspan="2" style="color: red; text-align: center; ">Não existem registros de consultas salvos para os dados acima.</td>
					</tr>
				</c:if>
					
				
				<%-- 
				-- Exibe a listagem com as classes que o usuário adicionou agora, mas que não foram salvas ainda. 
				--%>
				<c:if test="${not empty registraConsultasDiariasMateriaisMBean.listaClassesARegistrar}">
					<tr>
						<td colspan="2">
							<div class="infoAltRem" style="margin-bottom:0px;width:90%">
								<img src="${ctx}/img/delete.gif">: Remover Registros da Consulta
							</div>
							
							<table class="listagem" style="width:90%; margin-bottom: 20px;">
								<caption>Consultas a Registrar</caption>
								<thead>
									<tr>
										<th style="text-align:center;">Classe</th>
										<th style="text-align:center;">Quantidade</th>
										<th style="width:20px"></th>
									</tr>
								</thead>
								
								<c:forEach var="c" items="#{registraConsultasDiariasMateriaisMBean.listaClassesARegistrar}">
									<tr>
										<c:if test="${not empty c.classificacao1}">
											<td style="text-align:center;">${c.classificacao1} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao1} </sub></sub></td>
										</c:if>
										<c:if test="${not empty c.classificacao2}">
											<td style="text-align:center;">${c.classificacao2} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao2} </sub></sub></td>
										</c:if>
										<c:if test="${not empty c.classificacao3}">
											<td style="text-align:center;">${c.classificacao3} <sub><sub> ${classificacaoBibliograficaMBean.descricaoClassificacao3} </sub></sub></td>
										</c:if>
										
										<td style="text-align:center;">${c.quantidade}</td>
										
										<td>
											<h:commandLink action="#{registraConsultasDiariasMateriaisMBean.removerClasseARegistrar}"
													title="Remover registro" id="btnRemoverRegistroAindaNaoRegistrado1" rendered="#{c.classificacao1 != null}">
												<h:graphicImage value="/img/delete.gif" alt="Remover registro" />
												<f:param name="classificacao1ParaRemocao" value="#{ c.classificacao1 }"/>
											</h:commandLink>
											
											<h:commandLink action="#{registraConsultasDiariasMateriaisMBean.removerClasseARegistrar}"
													title="Remover registro" id="btnRemoverRegistroAindaNaoRegistrado2" rendered="#{c.classificacao2 != null}">
												<h:graphicImage value="/img/delete.gif" alt="Remover registro" />
												<f:param name="classificacao2ParaRemocao" value="#{ c.classificacao2 }" />
												
											</h:commandLink>
											
											<h:commandLink action="#{registraConsultasDiariasMateriaisMBean.removerClasseARegistrar}"
													title="Remover registro" id="btnRemoverRegistroAindaNaoRegistrado3" rendered="#{c.classificacao3 != null}">
												<h:graphicImage value="/img/delete.gif" alt="Remover registro" />
												<f:param name="classificacao3ParaRemocao" value="#{ c.classificacao3 }" />
											</h:commandLink>
											
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</c:if>
				
				
				<%-- 
				 -- Formulário para o usuário adicionar novos registros   
				 -- O usuário só vai poder informar a classificação cadastrada no sistema e utilizada na biblioteca selecionada no campo box acima
				 --%>
				 
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<caption>Adicionar Classes Consultadas</caption>
					
							<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1 
									&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca != null 
									&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca.primeiraClassificacao}">
								<tr>
									<th class="obrigatorio" style="width:50%;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao1} do material:</th>
									<td>
										 <h:inputText id="inputTextclassificacao1"value="#{registraConsultasDiariasMateriaisMBean.classificacao1}" size="4" maxlength="5" />
									</td>
								</tr>
							</c:if>
							
							<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2
								&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca != null 
									&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca.segundaClassificacao}">
								<tr>
									<th class="obrigatorio" style="width:50%;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao2} do material:</th>
									<td>
										 <h:inputText id="inputTextclassificacao2" value="#{registraConsultasDiariasMateriaisMBean.classificacao2}" size="4" maxlength="5" />
									</td>
								</tr>
							</c:if>
							
							<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3
								&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca != null 
									&& registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca.terceiraClassificacao}">
								<tr>
									<th class="obrigatorio" style="width:50%;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao3}  do material:</th>
									<td>
										 <h:inputText id="inputTextclassificacao3" value="#{registraConsultasDiariasMateriaisMBean.classificacao3}" size="4" maxlength="5" />
									</td>
								</tr>
							</c:if>
							
							<c:if test="${registraConsultasDiariasMateriaisMBean.classificacaoDaBiblioteca != null }">
								<tr>
									<th class="obrigatorio">Quantidade consultada:</th>
									<td>
										<h:inputText id="quantidadeConsultada"value="#{registraConsultasDiariasMateriaisMBean.quantidade}" size="5"maxlength="5" onkeyup="return formatarInteiro(this);" />
									</td>
								</tr>
							</c:if>
							
							<tfoot>
								<tr>
									<td colspan="2">
										<h:commandButton id="btnAdicionarClasseQuantidade" value="Adicionar" action="#{registraConsultasDiariasMateriaisMBean.adicionarClasseQuantidade}" />
									</td>
								</tr>
							</tfoot>
					
						</table>
					
					</td>
				
				</tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Cadastrar" id="btnCadastrar"
								action="#{registraConsultasDiariasMateriaisMBean.cadastrar}" />
						<h:commandButton value="Cancelar" id="btnCancelar"
								action="#{registraConsultasDiariasMateriaisMBean.cancelar}" onclick="#{confirm}" />
						
						<%-- Botão utilizando para exbir as consultas registradas quando o usuário vai escolhendo as informação na tela --%>
						<h:commandButton id="exibirClasses" style="display:none;" action="#{registraConsultasDiariasMateriaisMBean.exibirClassesCadastradas}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>