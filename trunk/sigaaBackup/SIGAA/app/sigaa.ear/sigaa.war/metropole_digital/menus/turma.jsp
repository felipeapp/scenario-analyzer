<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
 		<li>Turma
            <ul>
               	<li><h:commandLink action="#{tutoriaIMD.preCadastrar}" value="Cadastrar" onclick="setAba('turma')"/></li>
               	<li><h:commandLink action="#{tutoriaIMD.preListar}" value="Listar/Alterar" onclick="setAba('turma')"/></li>
<%--                	<li><h:commandLink action="#{buscaTurmaBean.popularBuscaTecnico}" value="Consulta Geral de Oferta de Disciplinas" onclick="setAba('turma')"></h:commandLink></li> --%>
			</ul>
        </li>
        
        <li>Acompanhamento
            <ul>
               	<li>Extrato de Frequência por Turma</li>
               	<li>Extrato de Notas por Turma</li>
           </ul>
        </li>
   		
   		<li> Tutores
   			<ul>
   				<li><a href="${ctx}/metropole_digital/pessoa/busca_geral.jsf?aba=turma">Cadastrar</a></li>
	   			<li><a href="${ctx}/metropole_digital/tutor_imd/lista.jsf?aba=turma">Listar/Alterar</a></li>
	   			<li><a href="${ctx}/metropole_digital/tutor_imd/logar_como.jsf?aba=turma">Logar como tutor</a></li>
   			</ul>
   		</li>
   		
   		
    </ul>
