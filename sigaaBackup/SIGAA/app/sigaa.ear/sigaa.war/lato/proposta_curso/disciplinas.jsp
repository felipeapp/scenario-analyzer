<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Disciplinas</h2>

<%@include file="include/_operacao.jsp"%>

<center>
	<div class="infoAltRem">
	    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar 
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
</center>

<h:form id="form">
  <table class=formulario width="100%">
   <caption class="listagem">Cadastro de Disciplina</caption>
	 <tr>
		<td colspan="4">
	       <div id="abas-disciplina">
  			  <div id="nova" class="aba">
				<table class="subFormulario" width="100%">
				  <caption>Nova Disciplina</caption>
					<tr>
						<th class="obrigatorio" width="1%">Nome:</th>
						<td colspan="8">
							<h:inputText id="nomeDisciplina" value="#{cursoLatoMBean.obj.disciplina.detalhes.nome}" size="88" maxlength="85" onkeyup="CAPS(this)" 
								disabled="#{cursoLatoMBean.obj.disciplina.id != 0}"/>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio" width="15%">Carga Horária:</th>
						<td>Aula:</td>
						<td>
							<h:inputText id="chaula" value="#{cursoLatoMBean.chAula}" size="6" maxlength="6" onkeyup="return formatarInteiro(this);"
								disabled="#{cursoLatoMBean.obj.disciplina.id != 0}"/>
						</td>
					</tr><tr>
						<td></td>
						<td width="10%">Laboratório:</td>
						<td>
							<h:inputText id="chlaboratorio" value="#{cursoLatoMBean.chLaboratorio}" size="6" maxlength="6" onkeyup="return formatarInteiro(this);"
								disabled="#{cursoLatoMBean.obj.disciplina.id != 0}" />
						</td>
					</tr><tr>
						<td></td>
						<td>Estágio: </td>
						<td>
							<h:inputText id="chestagio" value="#{cursoLatoMBean.chEstagio}" size="6" maxlength="6" onkeyup="return formatarInteiro(this);"
							disabled="#{cursoLatoMBean.obj.disciplina.id != 0}" />
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Ementa:</th>
						<td colspan="8">
							<h:inputTextarea id="ementa" value="#{cursoLatoMBean.obj.disciplina.detalhes.ementa}" rows="6" cols="85"
							disabled="#{cursoLatoMBean.obj.disciplina.id != 0}"/>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Bibliografia:</th>
						<td colspan="8">
							<h:inputTextarea id="bibliografia" value="#{cursoLatoMBean.obj.disciplina.bibliografia}" rows="6" cols="85"
							disabled="#{cursoLatoMBean.obj.disciplina.id != 0}"/>
						</td>
					</tr>
				</table>
		   </div>
			   <div id="antiga" class="aba">
					   <table class="subFormulario" width="100%" style="position:relative;">
						  <caption>Disciplina Antiga</caption>
							<tr>
								<th class="obrigatorio" width="10%">Nome:</th>
								<td>
									<h:inputText value="#{cursoLatoMBean.descricaoDisciplina}" id="nomeComponente" style="width: 440px;"/> 
									<rich:suggestionbox id="suggestion" width="400" height="120" for="nomeComponente" 
										minChars="3" suggestionAction="#{componenteCurricular.autocompleteComponenteCurricular}" var="_componente" fetchValue="#{_componente.nome}">
	
										<h:column>
											<h:outputText value="#{_componente.id} - " />
											<h:outputText value="#{_componente.codigo} - " /> 
											<h:outputText value="#{_componente.nome} - "/>
											(<h:outputText value="#{_componente.detalhes.chTotal} hrs" />)   
										</h:column>
	
										<f:param name="apenasDepartamento" value="false"/>
										<f:param name="nivelPermitido" value="L"/>
										
										<a4j:support event="onselect">
											<f:param name="apenasDepartamento" value="false"/>
											<f:param name="nivelPermitido" value="L"/>
										
											<f:setPropertyActionListener value="#{ _componente.id }" target="#{ cursoLatoMBean.idComponente }"/>
										</a4j:support>
										
									</rich:suggestionbox>
								</td>	
							</tr>
						</table>
			  	</div>
		  </div>
	   </td>
	  </tr>
   </table>
	
   	<table class="formulario" width="100%">
		<caption>Corpo Docente do Curso</caption>
			<tr>
				<th class="obrigatorio" width="16%">
					<h:outputText value="Corpo Docente:" id="docentes"/>
				</th>
				<td width="10%">
					<h:selectOneMenu value="#{cursoLatoMBean.corpoDocenteCursoLato.id}"
					 id="docenteIntDisciplina">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{cursoLatoMBean.obj.docentes}" />
					</h:selectOneMenu>
				</td>

				<th class="obrigatorio" width="25%">Carga Horária Dedicada:</th>
				<td width="10%" style="text-align: right;">
					<h:inputText id="chDedicada" value="#{cursoLatoMBean.obj.equipeLato.cargaHoraria}" size="6" maxlength="6" onkeyup="return formatarInteiro(this);"/>
				</td>
				<td>
					<h:commandLink action="#{cursoLatoMBean.adicionarDocenteDisciplina}">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Docente" />
					</h:commandLink>
				</td>
			</tr>

<c:if test="${not empty cursoLatoMBean.obj.equipesLato}">	
	<table class="subFormulario" width="100%" id="equipes">
	  <caption>Lista de Docentes Adicionados</caption>
		<thead>
			<tr>
				<th>Nome do Docente</th>
				<th style="text-align: right;">Carga Horária Dedicada</th>
				<th></th>
			</tr>
		</thead>

   		<c:forEach items="#{cursoLatoMBean.obj.equipesLato}" var="equipeLato">
          		<tr>
       				<td width="35%">${equipeLato.nomeDocente}</td>
               		<td width="35%" style="text-align: right;">${equipeLato.cargaHoraria}</td>
			  		<td align="right">
	              		<h:commandLink actionListener="#{cursoLatoMBean.removerDocenteDisciplina}" onclick="#{confirmDelete}">
				  			<f:attribute name="linha" value="#{equipeLato}"/>
							<h:graphicImage value="/img/delete.gif"style="overflow: visible;" title="Remover Docente"/>
						</h:commandLink>
					</td>
              	</tr>
              </c:forEach>
		<tfoot>
			<tr>
				<td colspan="8" align="center">
					<h:commandButton value="Adicionar Disciplina" action="#{cursoLatoMBean.adicionarDisciplina}" 
					id="adicionarDocentes" title="Adicionar Disciplina" />
				</td>
			</tr>
		</tfoot>
	</table>
  </c:if>
</table>	
	<br /><br />

	<c:if test="${not empty cursoLatoMBean.allDisciplinasCursoLato}">			
	<table class="formulario" width="100%">
		<caption>Disciplinas do Curso (${fn:length(cursoLatoMBean.allDisciplinasCursoLato)})</caption>
		<thead>
			<tr>
				<th width="15%">Código</th>
				<th width="50%">Nome</th>
				<th width="20%" style="text-align: right;">Carga Horária</th>
				<th width="10%"></th>
			</tr>
		</thead>

       <c:forEach items="#{cursoLatoMBean.allDisciplinasCursoLato}" var="ccl" varStatus="status">
            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
            	<td>${ccl.codigo}</td>
	            <td>${ccl.nomeCurso}</td>
                <td style="text-align: right;">${ccl.cargaHorariaTotal} h</td>
    			<td align="right">
    				<h:commandLink action="#{cursoLatoMBean.removerDisciplina}" onclick="#{confirmDelete}" >
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Disciplina" />
				   		<f:param name="id" value="#{ccl.idDisciplina}"/> 
					</h:commandLink>
                </td>
            </tr>
            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
            	<td colspan="4"><b>Ementa:</b><br />${ccl.ementa}</td>
            </tr>
            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
            	<td colspan="4"><b>Bibliografia:</b><br />${ccl.bibliografia}</td>
            </tr>
            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
            	<td colspan="2"><b>Docente(s): </b></td>
            	<td colspan="2"></td>
            </tr>
          		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	  
          			<c:forEach items="#{ccl.nomeDocente}" var="nomes">
          				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
          					<td colspan="2">${nomes.key}</td>
          					<td style="text-align: right;">${nomes.value} h</td>
          					<td></td>
          				</tr>
          			</c:forEach>
          		</tr>
       </c:forEach>
   </table>
  </c:if>

  <table class="formulario" width="100%">
		<tfoot>
		   <tr>
				<td colspan="4">
					<h:commandButton value="<< Voltar" action="#{cursoLatoMBean.telaAnterior}" id="voltar" />
					<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avançar >>" action="#{cursoLatoMBean.resumo}" id="cadastrar" />
				</td>
		   </tr>
		</tfoot>
  </table>
		     
</h:form>
 
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-disciplina');
        abas.addTab('nova', "Nova Disciplina");
        abas.addTab('antiga', "Antiga Disciplina");
        abas.activate('nova');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>