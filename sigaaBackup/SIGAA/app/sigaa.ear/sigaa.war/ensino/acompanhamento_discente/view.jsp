<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/ensino/acompanhamento_discente.css"/>
<style>
 table tbody tr th {
    font-weight: bold;
    text-align: right;
 }
</style>
<f:view>
	<h2> <ufrn:subSistema /> > Acompanhamento Acadêmico do Discente  </h2>
	
	<h:form>
	<div id="opcoes-bolsista">
		<table class="visualizacao">
			<tr>
				<td rowspan="20" align="center" style="vertical-align: top;">
					<c:if test="${acompanhamentoAcademicoDiscenteMBean.obj.idFoto != null}">
						<img src="${ctx}/verFoto?idFoto=<h:outputText value="#{acompanhamentoAcademicoDiscenteMBean.obj.idFoto}"/>
							&key=${ sf:generateArquivoKey(acompanhamentoAcademicoDiscenteMBean.obj.idFoto) }" width="100" height="125"/>
					</c:if>
					<c:if test="${acompanhamentoAcademicoDiscenteMBean.obj.idFoto == null}">
						<img src="${ctx}/img/no_picture.png" width="100" height="125"/>
					</c:if>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"> Dados do Discente </td>
			</tr> 
			<tr>
				<th> Matrícula:</th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.matricula }" /> </td>
			</tr>
			<tr>
				<th> Nome: </th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.nomeOficial }" /> </td>
			</tr>
			<c:if test="${ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.nomeOficial != acompanhamentoAcademicoDiscenteMBean.obj.pessoa.nome }">
				<tr>
					<th> Nome Social: </th>
					<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.nome }" /> </td>
				</tr>				
			</c:if>
			<tr>
				<a4j:region rendered="#{ acompanhamentoAcademicoDiscenteMBean.obj.graduacao }">
					<th>Matriz Curricular:</th>
					<td> 
						<h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.matrizCurricular }" /> 
					</td>
				</a4j:region>
				<a4j:region rendered="#{ not acompanhamentoAcademicoDiscenteMBean.obj.graduacao }">
					<th>Curso:</th>
					<td> 
						<h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.curso.nome }" /> 
					</td>
				</a4j:region>
			</tr>
			<tr>
				<th> Currículo: </th>
				<td>  
					<h:commandLink id="relatorio" styleClass="noborder" title="Relatório da Estrutura Curricular" action="#{curriculo.gerarRelatorioCurriculo}" value="#{acompanhamentoAcademicoDiscenteMBean.obj.curriculo.codigo}">
						<f:param name="id" value="#{acompanhamentoAcademicoDiscenteMBean.obj.curriculo.id}" />
					</h:commandLink>					
				</td>
			</tr>
			<tr>
				<th>Forma de Ingresso:</th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.formaIngresso.descricao }" /> </td>
			</tr>
			<tr>
				<th>Situação:</th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.statusString }" /> </td>
			</tr>
			
			<tr>
				<tr> <td colspan="2" class="subFormulario"> Contatos </td> </tr>
			</tr>
			<tr>
				<th> Telefone:</th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.telefone }" /> </td>
			</tr>
			<tr>
				<th> Celular:</th>
				<td> <h:outputText value="#{ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.celular }" /> </td>
			</tr>
			<tr>
				<th> E-mail:</th>
				<td> <a href="mailto:${ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.email }"> ${ acompanhamentoAcademicoDiscenteMBean.obj.pessoa.email } </a></td>
			</tr>
		</table>
	</div>

	<div id="opcoes-bolsista">
		<div class="item historicos">
			<h:commandLink action="#{acompanhamentoAcademicoDiscenteMBean.gerarHistorico}">
				Emitir Histórico
			</h:commandLink>
		</div>		
		<div class="item atestado">
			<h:commandLink action="#{acompanhamentoAcademicoDiscenteMBean.gerarAtestadoMatricula}">
				Emitir Atestado de Matrícula
			</h:commandLink>				
		</div>
		<div class="item notas">
			<h:commandLink action="#{relatorioNotasAluno.gerarRelatorio}">
				<f:param name="discente" value="#{acompanhamentoAcademicoDiscenteMBean.obj.id}"/>
				Consultar Notas
			</h:commandLink>
		</div>
		<div class="item bolsas">
			<h:commandLink action="#{acompanhamentoAcademicoDiscenteMBean.verBolsas}">
				 Consultar Bolsas e Auxílios
			</h:commandLink>
		</div>
		<div class="item msg">
			<h:commandLink action="#{acompanhamentoAcademicoDiscenteMBean.enviarMensagem}">
				 Enviar Mensagem
			</h:commandLink>
		</div>
		<div class="clear"> </div>
	</div>
		
		
	<div style="text-align: center;">
		<h:commandButton action="#{acompanhamentoAcademicoDiscenteMBean.iniciar}" value="<< Selecionar outro discente" />
	</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>