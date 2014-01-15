<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	
	<a4j:keepAlive beanName="bibliotecaMBean"></a4j:keepAlive>
	
	<h2>  <ufrn:subSistema /> &gt; Configurações da Biblioteca</h2>
	<br>
	<h:form id="formConfiguracoesBiblioteca">

		<div class="descricaoOperacao">
			<p>Utilize este formulário para alterar os dados da biblioteca, assim como definir quais operações ela que realiza </p>
			<br/>
			<p>
				As unidades mostradas em azul, são as unidades consideradas no calculo do emprétimos para saber se o aluno pertence ao "mesmo centro" da biblioteca.
				Para as bibliotecas não especializadas, é utilizada a unidade gestora que corresponde ao centro onde a biblioteca está vinculada.
				No caso das bibliotecas de unidades especializadas, elas não estão vinculadas a nenhum centro, então são considerados "do mesmo centro" os alunos cujo curso esteja ligado à unidade responsável da biblioteca.
				Essa regra não é válida para a biblioteca central.
			</p>
		</div>


		<%-- Observação:  Os teste de permissão de alteração só funcionam com esses 2 c:if, se colocar disable do <h:inputText não submete os dados, mesmo tendo valor false --%>

		<table class="formulario" width="95%">
			<caption>Alterar dados da Biblioteca</caption>
			
			<tr>
				<th class="obrigatorio" style="width: 30%">Descrição:</th>
				<td style="width: 70%"><h:inputText id="inputTextDescricaoBiblioteca" value="#{bibliotecaMBean.obj.descricao}" readonly="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" maxlength="200" size="70" /></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Identificador:</th>
				<td><h:inputText id="inputTextIdentificadorBiblioteca" value="#{bibliotecaMBean.obj.identificador}" readonly="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" maxlength="20" size="20" onkeyup="CAPS(this)" /></td>
			</tr>
			
			<tr>
				<th>E-mail:</th>
				<td><h:inputText id="inputTextEmailBiblioteca" value="#{bibliotecaMBean.obj.email}" readonly="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" maxlength="100" size="40" /></td>
			</tr>
			
			<tr>
				<th style="font-style: italic; padding-top: 10px; vertical-align: center;">Unidade:</th>
				<td style="font-style: italic; padding-top: 10px; vertical-align: center;">	${bibliotecaMBean.obj.unidade.codigoFormatado}  -  ${bibliotecaMBean.obj.unidade.nome}</td>
			</tr>
			
			<c:if test="${! bibliotecaMBean.obj.bibliotecaCentral}">
				<c:set var="_unidade_especializada" value="${bibliotecaMBean.obj.unidade.unidadeResponsavel.unidadeAcademicaEspecializada}"  scope="request"></c:set>
				
				<tr>
					<th style="font-style: italic; padding-top: 10px; vertical-align: center; ${_unidade_especializada ? 'color:blue;' : ''}">Unidade Responsável:</th>
					<td style="font-style: italic; padding-top: 10px; vertical-align: center; ${_unidade_especializada ? 'color:blue;' : ''}">
						${bibliotecaMBean.obj.unidade.unidadeResponsavel.codigoFormatado}  -  ${bibliotecaMBean.obj.unidade.unidadeResponsavel.nome}
						<c:if test="${_unidade_especializada}">
							(ESPECIALIZADA)
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th style="font-style: italic; padding-top: 10px; padding-bottom: 30px; vertical-align: center; ${! _unidade_especializada ? 'color:blue;' : ''}">Unidade Gestora:</th>
					<td style="font-style: italic; padding-top: 10px; padding-bottom: 30px; vertical-align: center; ${! _unidade_especializada ? 'color:blue' : ''}">${bibliotecaMBean.obj.unidade.gestora.codigoFormatado}  -  ${bibliotecaMBean.obj.unidade.gestora.nome}</td>
				</tr>
			</c:if>
		</table>
		
		
		
		
		
		<table class="formulario" width="95%">
			
			
			<tr style="height: 20px; text-align: center; font-weight: bold;">
				<td colspan="2"> Serviços de Empréstimos da Biblioteca </td>
			</tr>
			
			<tr>
				<th style="width: 40%">Serviços de Empréstimos está Ativo ? </th>
				
				<td style="width: 60%">
					<h:selectOneRadio id="radioButonRealizaEmprestimos" value="#{bibliotecaMBean.obj.servicoEmprestimosAtivos}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
						<a4j:support actionListener="#{bibliotecaMBean.atualizaPagina}" event="onclick" reRender="formConfiguracoesBiblioteca" />
					</h:selectOneRadio>
				</td>
				
			</tr>
			
			<c:if test="${bibliotecaMBean.obj.servicoEmprestimosAtivos}">
			
			<tr>
				<th>Realiza Empréstimos Institucional entre Bibliotecas Internas ?</th>
				
				<td>
					<h:selectOneRadio id="radioButonRealizaEmpretimosEntreBibliotecasInternas" value="#{bibliotecaMBean.servicoEmprestimoInstitucionalInterna.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
				
			</tr>
			
			<tr>
				<th>Realiza Empréstimos Institucional entre Bibliotecas Externas ?</th>
				
				<td>
					<h:selectOneRadio id="radioButonRealizaEmpretimosEntreBibliotecasExternas" value="#{bibliotecaMBean.servicoEmprestimoInstitucionalExterna.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
				
			</tr>
			
			<c:if test="${! bibliotecaMBean.obj.bibliotecaCentral}"> <%-- Não faz sentido configurar isso para a central, pois ela não está vinculada a nenhum centro --%>
				<tr>
					<th style="vertical-align: top;">Empresta para Alunos de Infantil/Técnico/Graduação do Mesmo Centro?</th>
					
					<td>
						<h:selectOneRadio id="radioButonEmprestaAlunoGraduacaoMesmoCentro" 
							value="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoMesmoCentro.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
							<a4j:support actionListener="#{bibliotecaMBean.atualizaPagina}" event="onclick" reRender="formConfiguracoesBiblioteca" />
						</h:selectOneRadio>
						
						<t:div style="padding-left: 50px;" rendered="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoMesmoCentro.ativo}" >
							<strong>Tipos de Empréstimo Realizados: </strong> <br/>
							<c:forEach var="tipoEmprestimo1" items="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoMesmoCentro.tiposEmprestimos}">
								<h:selectBooleanCheckbox id="checkboxEmprestaAlunosGraduacaoMesmoCentro" value="#{tipoEmprestimo1.selecionado}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}"  /> ${tipoEmprestimo1.descricao}   <br/> 
							</c:forEach>
						</t:div>
						
					</td>
					
				</tr>
				
				<tr>
					<th style="vertical-align: top;">Empresta para Alunos de Infantil/Técnico/Graduação de Outro Centro?</th>
					
					
					<td>
						<h:selectOneRadio id="radioButonEmprestaAlunoGraduacaoOutroCentro" value="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoOutroCentro.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
							<a4j:support actionListener="#{bibliotecaMBean.atualizaPagina}" event="onclick" reRender="formConfiguracoesBiblioteca" />
						</h:selectOneRadio>
						
						<t:div style="padding-left: 50px;" rendered="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoOutroCentro.ativo}" >
							<strong>Tipos de Empréstimo Realizados: </strong> <br/>
							<c:forEach var="tipoEmprestimo2" items="#{bibliotecaMBean.servicoEmprestimoAlunoGraduacaoOutroCentro.tiposEmprestimos}">
								<h:selectBooleanCheckbox id="checkboxEmprestaAlunosGraduacaoOutroCentro" value="#{tipoEmprestimo2.selecionado}"  disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" /> ${tipoEmprestimo2.descricao}   <br/> 
							</c:forEach>
						</t:div>
						
					</td>
					
				</tr>
				
				
				<tr>
					<th style="vertical-align: top;">Empresta para Alunos de Pós-graduação do Mesmo Centro?</th>
					
					<td>
						<h:selectOneRadio id="radioButonEmprestaAlunoPostMesmoCentro" value="#{bibliotecaMBean.servicoEmprestimoAlunoPosMesmoCentro.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
							<a4j:support actionListener="#{bibliotecaMBean.atualizaPagina}" event="onclick" reRender="formConfiguracoesBiblioteca" />
						</h:selectOneRadio>
						
						<t:div style="padding-left: 50px;" rendered="#{bibliotecaMBean.servicoEmprestimoAlunoPosMesmoCentro.ativo}" >
							<strong>Tipos de Empréstimo Realizados: </strong> <br/>
							<c:forEach var="tipoEmprestimo3" items="#{bibliotecaMBean.servicoEmprestimoAlunoPosMesmoCentro.tiposEmprestimos}">
								<h:selectBooleanCheckbox id="checkboxEmprestaAlunosPosMesmoCentro" value="#{tipoEmprestimo3.selecionado}"  disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" /> ${tipoEmprestimo3.descricao}   <br/> 
							</c:forEach>
						</t:div>
						
					</td>
					
				</tr>
				
				<tr>
					<th style="vertical-align: top;">Empresta para Alunos de Pós-graduação de Outro Centro?</th>
					
					<td>
						<h:selectOneRadio id="radioButonEmprestaAlunoPostOutroCentro" value="#{bibliotecaMBean.servicoEmprestimoAlunoPosOutroCentro.ativo}" disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
							<a4j:support actionListener="#{bibliotecaMBean.atualizaPagina}" event="onclick" reRender="formConfiguracoesBiblioteca" />
						</h:selectOneRadio>
						
						<t:div style="padding-left: 50px;" rendered="#{bibliotecaMBean.servicoEmprestimoAlunoPosOutroCentro.ativo}" >
							<strong>Tipos de Empréstimo Realizados: </strong> <br/>
							<c:forEach var="tipoEmprestimo4" items="#{bibliotecaMBean.servicoEmprestimoAlunoPosOutroCentro.tiposEmprestimos}">
								<h:selectBooleanCheckbox id="checkboxEmprestaAlunosPosOutroCentro" value="#{tipoEmprestimo4.selecionado}"  disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}" /> ${tipoEmprestimo4.descricao}   <br/> 
							</c:forEach>
						</t:div>
						
					</td>
					
				</tr>
				
				</c:if>
			</c:if>
			
		
		</table>
		
		<table class="formulario" width="95%">
			
			<tr style="height: 20px; text-align: center; font-weight: bold;">
				<td colspan="2"> Serviços aos Usuários da Biblioteca </td>
			</tr>
			
			<tr>
				<th style="width: 40%;">Realiza Normalização?</th>
				<td style="width: 60%;">
					<h:selectOneRadio
						id="realizaNormalizacao"
						value="#{bibliotecaMBean.obj.servicos.realizaNormalizacao}"
						disabled="#{!bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th style="width: 40%;">Realiza Orientação de Normalização?</th>
				<td style="width: 60%;">
					<h:selectOneRadio 
						id="realizaOrientacao"
						value="#{bibliotecaMBean.obj.servicos.realizaOrientacaoNormalizacao}" 
						disabled="#{!bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim"/>
						<f:selectItem itemValue="false" itemLabel="Não"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th style="width: 40%;">Realiza Catalogação?</th>
				<td style="width: 60%;">
					<h:selectOneRadio
						id="realizaCatalogacao"
						value="#{bibliotecaMBean.obj.servicos.realizaCatalogacaoNaFonte}"
						disabled="#{!bibliotecaMBean.usuarioTemPermissaoAlteracao}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<%--
			
			Estes casos de uso não estão em produção, só descomentar depois que forem para produção.
			
			<tr>
				<th>Realiza Levantamento Bibliográfico?</th>
				
				<c:if test="${! permissaoAlteracaoBiblioteca}">
					<td><h:selectOneRadio
							value="#{bibliotecaMBean.obj.servicos.realizaLevantamentoBibliografico}"
							disabled="#{true}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio></td>
				</c:if>
				
				<c:if test="${permissaoAlteracaoBiblioteca}">
					<td><h:selectOneRadio
							value="#{bibliotecaMBean.obj.servicos.realizaLevantamentoBibliografico}"
							readonly="#{bibliotecaMBean.readOnly}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio></td>
				</c:if>
			</tr>
			
			<tr style="height: 20px;">
				<td colspan="2"> <hr/></td>
			</tr>
			
			<tr>
				<th>Realiza Levantamento de Infra-Estrutura?</th>
				
				<c:if test="${! permissaoAlteracaoBiblioteca}">
					<td><h:selectOneRadio
						value="#{bibliotecaMBean.obj.servicos.realizaLevantamentoInfraEstrutura}"
						disabled="#{true}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio></td>
				</c:if>
				
				<c:if test="${permissaoAlteracaoBiblioteca}">
					<td><h:selectOneRadio id="radioButonRealizaLevamentamentoInfra"
						value="#{bibliotecaMBean.obj.servicos.realizaLevantamentoInfraEstrutura}"
						readonly="#{bibliotecaMBean.readOnly}">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio></td>
				</c:if>
			</tr>
			
			--%>
			
		</table>
		
		<table class="formulario" width="95%">
			
			<tr>
				<th style="width: 40%;">Funciona no Sábado?</th>
				
				<td style="width: 60%;">
					<h:selectOneRadio id="radioButonFuncionaSabado"
					value="#{bibliotecaMBean.obj.funcionaSabado}"
					disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
					<f:selectItem itemValue="true" itemLabel="Sim" />
					<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
				
			</tr>
			<tr>
				<th>Funciona no Domingo?</th>
			
				<td>
					<h:selectOneRadio id="radioButonFuncionaDomingo"
					value="#{bibliotecaMBean.obj.funcionaDomingo}"
					disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
					<f:selectItem itemValue="true" itemLabel="Sim" />
					<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
				
			</tr>
			
			<tr>
				<th>Visualização pública do acervo?</th>
				
				<td>
				<h:selectOneRadio id="radioVisualizacaoPublicaAcervo"
					value="#{bibliotecaMBean.obj.acervoPublico}"
					disabled="#{! bibliotecaMBean.usuarioTemPermissaoAlteracao}">
					<f:selectItem itemValue="true" itemLabel="Sim" />
					<f:selectItem itemValue="false" itemLabel="Não" />
				</h:selectOneRadio>
				</td>
				
			</tr>
			
			<tr style="height: 20px;">
				<td colspan="2"></td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL} %>">
						<c:if test="${bibliotecaMBean.usuarioTemPermissaoAlteracao}">
							<h:commandButton id="cmdButonAlterarDados" value="#{bibliotecaMBean.confirmButton}" action="#{bibliotecaMBean.cadastrar}" />
						</c:if>
					</ufrn:checkRole>
					
					<h:commandButton id="cmdButtonVoltar" value="<< Voltar" action="#{ bibliotecaMBean.voltar }" />
					<h:commandButton id="cmdButtonCancelar" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{bibliotecaMBean.cancelar}" />
				</td>
			</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>