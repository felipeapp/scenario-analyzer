<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<table width="100%" class="subFormulario">
	<tr>
		<td width="40"><html:img page="/img/help.png" /></td>
		<td valign="top" style="text-align: justify">
			Para adicionar uma nova disciplina, preencha os dados, selecione um docente, defina a carga horária
			que ele dedicará à disciplina e, então, clique em <b>Adicionar Docente</b>.  Quando preencher
			todos os campos da disciplina e tiver adicionado todos os docentes que a ministrarão,
			com suas respectivas cargas horárias, clique em <b>Adicionar Disciplina</b>. <br />
		</td>
	</tr>
</table>

<br />
<script type="text/javascript">
	function docenteInterno(bool) {

		if (bool) {
			Element.show('docenteInterno');
			Element.hide('docenteExterno');
		} else {
			Element.show('docenteExterno');
			Element.hide('docenteInterno');
		}
	}
</script>

<html:form action="/ensino/latosensu/criarCurso" method="post">

<table class="formulario" width="100%">
	<caption>Cadastro de Disciplinas</caption>
	<tr>
		<td colspan="2">
			<div id="tabs-disciplinas">
				<div id="nova" class="tab-content">
					<table class=subformulario" width="100%">
						<caption>Dados da Disciplina</caption>
						<html:hidden property="disciplina.id" />
						<tr>
							<th class="required">Nome:</th>
							<td><html:text property="disciplina.detalhes.nome" size="88" maxlength="85" onkeyup="CAPS(this)" /></td>
						</tr>
						<tr>
							<th class="required">Carga Horária:</th>
							<td>&nbsp;&nbsp;&nbsp;<i>Aula:</i>
	        				    <html:text property="disciplina.detalhes.chAula" size="6" maxlength="6" />
	            				&nbsp;&nbsp;&nbsp;<i>Laboratório:</i>
	           					<html:text property="disciplina.detalhes.chLaboratorio" size="6" maxlength="6" />
	           					&nbsp;&nbsp;&nbsp;<i>Estágio:</i>
	            				<html:text property="disciplina.detalhes.chEstagio" size="6" maxlength="6" />
							</td>
						</tr>
						<tr>
							<th class="required">Ementa:</th>
							<td><html:textarea property="disciplina.detalhes.ementa" cols="85" rows="6" /></td>
						</tr>
						<tr>
							<th class="required">Bibliografia:</th>
							<td><html:textarea property="disciplina.bibliografia" cols="85" rows="6" /></td>
						</tr>
					</table>
				</div>
				<div id="existente" class="tab-content">
					<table class="subFormulario" width="100%">
						<caption>Buscar Disciplina</caption>
						<tr>
							<th>Nome:</th>
							<td>
								<c:set var="obrigatorio" value="true" />
								<c:set var="idAjax" value="disciplinaAjax.id" />
								<c:set var="nomeAjax" value="disciplinaAjax.detalhes.nome" />
								<c:set var="nivelEnsino" value="L" />
								<%@include file="/WEB-INF/jsp/include/ajax/disciplina.jsp"%>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="2">
		<table class=subformulario" width="100%">
			<caption>Docentes Ministrantes da Disciplina</caption>
			<tr>
				<td colspan="6">
					<html:radio property="interno" styleClass="noborder" value="true" 
							onclick="javascript:docenteInterno(true);" />Docente ${ configSistema['siglaInstituicao'] }
   					<html:radio property="interno" styleClass="noborder" value="false" 
   							onclick="javascript:docenteInterno(false);" />Docente Externo
				</td>
			</tr>
			<tr>
				<th class="required">Docente:</th>
				<td id="docenteInterno">
					<html:select property="equipeLato.docente.id">
		        		<html:option value="">-- SELECIONE --</html:option>
		       			<html:options collection="docentesInternos" property="cursoServidorId" labelProperty="nome" />
		    		</html:select>
				</td>
				<td id="docenteExterno">
					<html:select property="equipeLato.docenteExterno.id">
		       			<html:option value="">-- SELECIONE --</html:option>
		       			<html:options collection="docentesExternos" property="cursoServidorId" labelProperty="nome" />
		    		</html:select>
				</td>
				<th class="required">Carga Horária Dedicada:</th>
				<td width="40%">
					<html:text property="equipeLato.cargaHoraria" size="5" maxlength="3" onkeyup="formatarInteiro(this)" />
				</td>
				<td />
			</tr>
			<tr style="background-color: #DCDCDC">
				<td colspan="6" align="center"><html:button dispatch="adicionarDocenteDisciplina" value="Adicionar Docente" /></td>
			</tr>
			<tr>
				<td colspan="6" align="center">
					<table class="listagem" width="60%">
		    			<caption>Lista de Docentes Adicionados</caption> 
		    	    	<thead>
		        			<tr>
			        			<td>Nome do Docente</td>
			        			<td>Carga Horária Dedicada</td>
			        			<td />
			    			</tr>
		       			</thead>
		        		<tbody>

						<c:choose> 
		  	  			<c:when test="${not empty cursoLatoForm.equipesLato}">  
			    		<c:forEach items="${cursoLatoForm.equipesLato}" var="equipeLato">
		            		<tr>
		            		<c:choose>
		            			<c:when test="${equipeLato.externo}">
		                    		<td>${equipeLato.docenteExterno.pessoa.nome}</td>
		                    		<td>${equipeLato.cargaHoraria}</td>
		                    		<td align="right">
		                       			<html:link action="/ensino/latosensu/criarCurso?dispatch=removerDocenteDisciplina&id=${equipeLato.id}" 
		                    		    		onclick="${confirmDelete}">
	                       					<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Professor desta Disciplina" 
	                       							title="Remover" border="0" />
		                        		</html:link>
		                    		</td>
		                		</c:when>
		                		<c:otherwise>
		                    		<td>${equipeLato.docente.pessoa.nome}</td>
		                    		<td>${equipeLato.cargaHoraria}</td>
		                    		<td align="right">
		                        		<html:link action="/ensino/latosensu/criarCurso?dispatch=removerDocenteDisciplina&id=${equipeLato.id}" 
		                        				onclick="${confirmDelete}">
	                       					<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Professor desta Turma" 
	                       							title="Remover" border="0" />
		                        		</html:link>
		                    		</td>
		                		</c:otherwise>
		            		</c:choose>
		            		</tr>
		        		</c:forEach>
		        		</c:when>
		 				<c:otherwise>
			    		<tr>
	        				<td colspan="5" align="center" style="color: red;">Nenhum Docente Adicionado.</td>	        
	    			  	</tr>
	        			</c:otherwise>
		        		</c:choose>
		    		</table>
				</td>
			</tr>
		</table>
	</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="adicionarDisciplina" value="Adicionar Disciplina" />
			</td>
		</tr>
	</tfoot>
</table>

<script>
	docenteInterno(${cursoLatoForm.interno});
</script>

<br />
	<div class="infoAltRem">
	    <html:img page="/img/delete.gif" style="overflow: visible;"/> : Remover esta Disciplina
	</div>
		<table class="listagem" width="80%">
		<caption class="listagem">Disciplinas do Curso</caption>
	        <thead>
	        <tr>
	        	<td>Código</td>
		        <td>Nome</td>
		        <td>Carga Horária</td>
		        <td></td>
		    </tr>
	        </thead>

		<c:choose> 
			<c:when test="${not empty cursoLatoForm.obj.componentesCursoLato}">  
	 		<c:set var="corpoDocente" value="${cursoLatoForm.docentesDisciplina}" />
	        <c:forEach items="${cursoLatoForm.obj.componentesCursoLato}" var="ccl" varStatus="status">
	            
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	            	<td>${ccl.disciplina.codigo}</td>
	                <td>${ccl.disciplina.nome}</td>
	                <td>${ccl.disciplina.chTotal} h</td>
		            <td align="right" rowspan="4">
	                	<html:link action="/ensino/latosensu/criarCurso?dispatch=removerDisciplina&id=${ccl.id}" onclick="${confirmDelete}">
                       		<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover esta Disciplina do curso" 
                       		title="Remover" border="0" />
	                    </html:link>
	                </td>
	            </tr>
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	            	<td colspan="3"><b>Ementa:</b><br />${ccl.disciplina.detalhes.ementa}</td>
	            </tr>
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
	            	<td colspan="3"><b>Bibliografia:</b><br />${ccl.disciplina.bibliografia}</td>
	            </tr>
	            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
	            	<td colspan="3"><b>Docente(s):</b></td>
	            </tr>
	            <c:choose>
	            	<c:when test="${not empty ccl.docentesNome}">
	            		<c:forEach var="nome" items="${ccl.docentesNome}">
	            		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	  
	            	 		<td>Nome:</td>          	
	            			<td>${nome}
	            		</tr>
	            		</c:forEach>
	            	</c:when>
	            	<c:otherwise>
	          			<c:forEach var="docente" items="${corpoDocente}">
	            			<c:if test="${docente.disciplina.id == ccl.disciplina.id}">
	            				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	  
	            	 				<td>Nome:</td>          	
	            					<td>${docente.nomeDocente}</td>
	            					<td>Carga Horária:</td>
	            					<td>${docente.cargaHoraria}h</td>
	            				</tr>
	            			</c:if>
	            		</c:forEach>
	            	</c:otherwise>
	            </c:choose>
	        </c:forEach>
			</c:when>
			<c:otherwise>
			<tr>
				<td colspan="5" align="center" style="color: red;">Nenhuma disciplina adicionada.</td>
			</tr>
			</c:otherwise>
		</c:choose>

<tfoot>
	<tr>
	  <td colspan="5" align="center">
			<html:button dispatch="gravar" value="Gravar"/>
			<html:button view="turmasEntrada" value="<< Voltar"/>
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			<html:button dispatch="coordenacaoCurso" value="Avançar >>"/>
		</td>
	</tr>
</tfoot>
</table>
<br />
<br />
</html:form>

<center>
	<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<script type="text/javascript">
var tabs;
var Tabs = {
	    init : function(){
	       	if (getEl('tabs-disciplinas')) {
	       		tabs = new YAHOO.ext.TabPanel('tabs-disciplinas');
		    	tabs.addTab('nova', "NOVA DISCIPLINA");
		        tabs.addTab('existente', "DISCIPLINA EXISTENTE");
	       		tabs.activate('nova');
	       }
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>