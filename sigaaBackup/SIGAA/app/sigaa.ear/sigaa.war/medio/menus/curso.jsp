<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
        <li>Curso
            <ul>
                <li><h:commandLink action="#{cursoMedio.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/> </li>
                <li><h:commandLink action="#{cursoMedio.listar}" value="Listar/Alterar" onclick="setAba('curso')"/> </li>
		        <li><h:commandLink action="#{calendarioMedioMBean.iniciar}" value="Calendário Acadêmico" id="btCalendarioAcademico" onclick="setAba('curso')"/></li>
            </ul>
        </li>
		<li>Série
        	<ul>
                <li> <h:commandLink action="#{serie.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/> </li>
                <li> <h:commandLink action="#{serie.listar}" value="Listar/Alterar" onclick="setAba('curso')"/> </li>
        	</ul>
        </li>
        <li>Disciplina
			<ul>
		        <li><h:commandLink action="#{disciplinaMedioMBean.iniciar}" value="Cadastrar" id="btCadastroDisciplina" onclick="setAba('curso')" /></li>
		        <li><h:commandLink action="#{disciplinaMedioMBean.listar}" value="Listar/Alterar" id="btListarDisciplina" onclick="setAba('curso')" /></li>
			</ul>
		</li>
     	<li>Estrutura Curricular
            <ul>
	            <li><h:commandLink action="#{curriculoMedio.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
	            <li><h:commandLink action="#{curriculoMedio.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
			</ul>
        </li>
        <li> Horário
			<ul>
		        <li><h:commandLink action="#{horario.iniciar}" value="Cadastrar" onclick="setAba('curso')" /></li>
		        <li><h:commandLink action="#{horario.listar}" value="Listar/Alterar" onclick="setAba('curso')" /></li>
			</ul>
		</li>
		<li>Modalidade de Curso
    		<ul>
    			<li><h:commandLink action="#{modalidadeCursoMedio.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
    			<li><h:commandLink action="#{modalidadeCursoMedio.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
    		</ul>
    	</li>
		<li> Operações Administrativas
    		<ul>
	    		<li><h:commandLink id="calendarioAcademico" action="#{calendarioMedioMBean.iniciar}" value="Calendário Acadêmico" onclick="setAba('curso')"/></li>
	    		<li><h:commandLink id="parametrosGestoraAcademica" action="#{parametros.iniciarMedio}" value="Parâmetros Acadêmicos" onclick="setAba('curso')"/> </li>
    		</ul>
    	</li>
    	<li>Docentes
			<ul>
				<li> <h:commandLink action="#{docenteExterno.preCadastrar}" value="Cadastrar Docente Externo" onclick="setAba('curso')"/> </li>
				<li> <h:commandLink action="#{docenteExterno.iniciarAlterar}" value="Consultar Docentes Externos"  onclick="setAba('curso')"/></li>
				<li> <ufrn:link action="administracao/docente_externo/lista.jsf" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO} %>"> Cadastrar Usuário Para Docente Externo</ufrn:link> </li>
				<li> <a href="${ctx}/ead/pessoa/lista.jsf?aba=pessoas" onclick="setAba('curso')">Alterar Dados Pessoais </a> </li>
			</ul>
		</li>
    </ul>