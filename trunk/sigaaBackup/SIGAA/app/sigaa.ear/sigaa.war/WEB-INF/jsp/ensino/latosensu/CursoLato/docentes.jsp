<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina"><ufrn:steps/></h2>

<script>
	function mostraEstrangeiro(obj) {

		if (obj) {
			Element.show('passaporte');
			Element.hide('cpf');
		} else {
			Element.show('cpf');
			Element.hide('passaporte');
		}

	};
	
	function isTecnico(obj) {
		if (obj)
			$('instituicao').value = 96;
		else
			$('instituicao').value = 0;
		
		$('instituicao').disabled = obj;
	};
</script>

<html:form action="/ensino/latosensu/criarCurso" method="post" onsubmit="return validateTecDocenteTurmaForm(this);">

	<div id="tabs-docentes">
		<div id="interno" class="tab-content">
		<br />
		
			<table class="formulario">
			<caption class="listagem">Buscar Docente</caption>
			<tbody>
				<tr>
					<th>Nome:</th>
					<td>
						<c:set var="tipo" value="todos" />
						<c:set var="idAjax" value="cursoServidor.servidor.id" />
						<c:set var="nomeAjax" value="cursoServidor.servidor.pessoa.nome" />
						<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<html:button dispatch="adicionarCursoServidor" value="Adicionar" />
					</td>
				</tr>
			</tfoot>
			</table>

		</div>
		
 		<div id="externo" class="tab-content">
 		<br />

			<table class="formulario">
			<caption>Dados do Docente</caption>
				<tbody>
					<tr>
						<th class="required">Nome:</th>
						<td><html:text property="docenteExterno.pessoa.nome" size="70" maxlength="100" onkeyup="CAPS(this)" /></td>
					</tr>
					<tr>
						<th class="required">Nome da Mãe:</th>
						<td><html:text property="docenteExterno.pessoa.nomeMae" size="70" maxlength="100" onkeyup="CAPS(this)" /></td>
					</tr>
					<tr>
						<th class="required">Email:</th>
						<td><html:text property="docenteExterno.pessoa.email" size="70" maxlength="60" /></td>
					</tr>
					<tr>
						<th class="required">Sexo: </th>
						<td>
							<html:radio property="docenteExterno.pessoa.sexo" value="M" label="Masculino" />
							<html:radio property="docenteExterno.pessoa.sexo" value="F" label="Feminino" />
						</td>
					</tr>
					<tr>
						<th>Estrangeiro:</th>
						<td>
							<html:radio property="estrangeiro" styleClass="noborder" value="true" 
									onclick="javascript:mostraEstrangeiro(true);" />Sim
		   					<html:radio property="estrangeiro" styleClass="noborder" value="false" 
		   							onclick="javascript:mostraEstrangeiro(false);" />Não
						</td>
					</tr>
					<tr>
						<th>Técnico da ${ configSistema['siglaInstituicao'] }:</th>
						<td>
							<html:radio property="tecnico" styleClass="noborder" value="true" onclick="javascript: isTecnico(true);" />Sim
		   					<html:radio property="tecnico" styleClass="noborder" value="false" onclick="javascript: isTecnico(false);" />Não
						</td>
					</tr>
					<tr id="cpf">
						<th class="required">CPF:</th>
						<td>
							<html:text property="cpf" maxlength="14" size="14" value="" onkeypress="formataCPF(this, event, null)" />
						</td>
					</tr>
					<tr id="passaporte">
						<th class="required">Passaporte:</th>
						<td><html:text property="docenteExterno.pessoa.passaporte" maxlength="20" size="20" /></td>
					</tr>
					<tr>
						<th class="required">Formação:</th>
						<td>
							<html:select property="docenteExterno.formacao.id">
		              			<html:option value=""> -- SELECIONE -- </html:option>
		              			<html:options collection="formacoes" property="id" labelProperty="denominacao" />
		             		</html:select>
						</td>
					</tr>
					<tr>
					<th class="required">Instituição:</th>
						<td>
							<html:select styleId="instituicao" property="docenteExterno.instituicao.id">
		              			<html:option value=""> -- SELECIONE -- </html:option>
		              			<html:options collection="instituicoes" property="id" labelProperty="descricao" />
		             		</html:select>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2"><html:button dispatch="adicionarCursoServidorExterno" value="Adicionar" /></td>
					</tr>
				</tfoot>
			</table>

			<script>
				mostraEstrangeiro(${cursoLatoForm.estrangeiro});
			</script>
		</div>
	</div>

	<c:if test="${not empty cursoLatoForm.obj.cursosServidores}">
		<br />
		<div class="infoAltRem">
	    	<html:img page="/img/delete.gif" style="overflow: visible;"/> : Remover este Docente
		</div>
	    <table class="listagem" width="80%">
		<caption class="listagem">Corpo Docente do Curso</caption>
	        <thead>
	        <tr>
	        	<td>Siape</td>
		        <td>Nome</td>
		        <td>Titulação</td>
		        <td>Instituição</td>
		        <td></td>
		    </tr>
	        </thead>
	        
	        <tbody>
	        <c:forEach items="${cursoLatoForm.obj.cursosServidores}" var="cursoServidor" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	            <c:choose>
	            	<c:when test="${cursoServidor.externo}">
						<td> - </td>
	                    <td>${cursoServidor.docenteExterno.pessoa.nome}</td>
	                    <td>${cursoServidor.docenteExterno.formacao.denominacao}</td>
	                    <td>${cursoServidor.docenteExterno.instituicao.sigla}</td>
	                    <td width="20">
	                        <%--<html:image dispatch="removerCursoServidorExterno" property="docenteId" alt="Remover Docente deste Curso" 
	                        title="Remover" value="${cursoServidor.servidor.id}"></html:image>--%>
	                        <html:link action="/ensino/latosensu/criarCurso?dispatch=removerCursoServidorExterno&id=${cursoServidor.id}&docenteExternoId=${cursoServidor.docenteExterno.id}" onclick="${confirmDelete}">
                       			<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Professor desta Turma" title="Remover" border="0" />
	                        </html:link>
	                    </td>
	                </c:when>
	                <c:otherwise>
	                	<td>${cursoServidor.servidor.siape}</td>
	                    <td>${cursoServidor.servidor.pessoa.nome}</td>
	                    <td>${cursoServidor.servidor.formacao.denominacao}</td>
	                    <td>${ configSistema['siglaInstituicao'] }</td>
	                    <td width="20">
	                        <%-- <html:image dispatch="removerCursoServidor" property="docenteId" alt="Remover Docente deste Curso" title="Remover" value="${cursoServidor.servidor.id}"></html:image>--%>
	                        <html:link action="/ensino/latosensu/criarCurso?dispatch=removerCursoServidor&id=${cursoServidor.id}&docenteId=${cursoServidor.servidor.id}" onclick="${confirmDelete}">
                       			<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Professor desta Turma" title="Remover" border="0" />
	                        </html:link>
	                    </td>
	                </c:otherwise>
	            </c:choose>
	            </tr>
	        </c:forEach>
	        </tbody>
			<tfoot>
				<tr>
				  <td align="center" colspan="6">
					<html:button dispatch="gravar" value="Gravar" />
					<html:button view="recursos" value="<< Voltar" />
					<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
					<html:button dispatch="turmasEntrada" value="Avançar >>" />
				  </td>
				</tr>
			</tfoot>
	    </table>
	</c:if>
	<br /><br />
</html:form>

<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<br />

<script type="text/javascript">
	var tabs;
	var Tabs = {
		init : function(){
		//	 alert(getEl('tabs-docentes'));
			if(getEl('tabs-docentes') !== null){
				tabs = new YAHOO.ext.TabPanel('tabs-docentes');
			    tabs.addTab('interno', "DOCENTE CADASTRADO (UFRN/EXTERNO)");
		    	tabs.addTab('externo', "CADASTRAR NOVO DOCENTE EXTERNO");
		    	tabs.activate('interno');
			}
		}
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
